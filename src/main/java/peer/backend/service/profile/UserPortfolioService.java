package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.SkillDTO;
import peer.backend.dto.profile.response.PortfolioDTO;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.tag.RecruitTag;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamUserStatus;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserPortfolio;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.TagRepository;
import peer.backend.repository.board.recruit.RecruitTagRepository;
import peer.backend.repository.board.team.PostRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.user.UserPortfolioRepository;
import peer.backend.repository.user.UserRepository;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPortfolioService {
    private final UserPortfolioRepository userPortfolioRepository;
    private final TeamRepository teamRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RecruitTagRepository recruitTagRepository;
    private final TagRepository tagRepository;

    @Transactional
    public void setTeamLogoPath(Long teamId, String path) {
        List<UserPortfolio> target = this.userPortfolioRepository.findByTeamId(teamId);
        if (!target.isEmpty()) {
            target.forEach(m -> m.setTeamLogo(path));
            this.userPortfolioRepository.saveAll(target);
        }
    }

    @Transactional
    public void setRecruitImagePath(Long recruitId, String path) {
        List<UserPortfolio> target = this.userPortfolioRepository.findByRecruitId(recruitId);
        if (!target.isEmpty()) {
            target.forEach(m -> m.setTeamLogo(path));
            this.userPortfolioRepository.saveAll(target);
        }
    }

    @Transactional
    public UserPortfolio makeUserPortfolio(User targetUser,
                                           Team targetTeam) {
        Recruit data = targetTeam.getRecruit();
        return UserPortfolio.builder()
                .userId(targetUser.getId())
                .user(targetUser)
                .teamId(targetTeam.getId())
                .team(targetTeam)
                .teamName(targetTeam.getName())
                .teamLogo(targetTeam.getTeamLogoPath())
                .recruitImage(data.getThumbnailUrl())
                .recruitId(data.getId())
                .recruit(data)
                .build();
    }

    @Transactional
    public void makeWholeTeamUserForPortfolio(Long teamId) {
        Team targetTeam = this.teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("팀을 찾을 수 없습니다."));
        List<TeamUser> targetUsers = targetTeam.getTeamUsers();
        if (targetUsers.isEmpty())
            throw new NotFoundException("해당하는 팀원들이 존재하지 않습니다.");
        List<UserPortfolio> portfolios = new ArrayList<>();
        for (TeamUser member: targetUsers) {
            if (member.getStatus().equals(TeamUserStatus.APPROVED)) {
                User memberUser = member.getUser();
                portfolios.add(this.makeUserPortfolio(memberUser, targetTeam));
            }
        }
        if (portfolios.isEmpty())
            throw new BadRequestException("포트폴리오 생성에 실패하였습니다.");
        this.userPortfolioRepository.saveAll(portfolios);
    }

    @Transactional
    public void setWholeTeamUserWithShowcaseCreation(Long teamId,
                                                     Long postId){
        Team targetTeam = this.teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("팀을 찾을 수 없습니다."));
        List<UserPortfolio> portfolios = targetTeam.getUserPortfolioHistories();
        Post post = this.postRepository
                .findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));
        portfolios.forEach(one -> {
            one.setShowcaseId(post.getId());
            one.setPost(post);
        });
        this.userPortfolioRepository.saveAll(portfolios);
    }

    @Transactional
    public void setVisibilityForMyPortfolioLogic(User user
            , boolean value)
            throws BadRequestException {
        if (user.isVisibilityForPortfolio() == value) {
            throw new BadRequestException("비정상적인 갑 변경입니다.");
        }
        user.setVisibilityForPortfolio(value);
        user = userRepository.saveAndFlush(user);

        List<UserPortfolio> portfolios = user.getMyPortfolios();
        portfolios.forEach(m -> m.setVisibility(value));

        userPortfolioRepository.saveAllAndFlush(portfolios);
    }

    private PortfolioDTO makePortfolioDTO(UserPortfolio target, boolean last, boolean other) {
        List<Long> redirectionIds = new ArrayList<>();
        redirectionIds.add(target.getRecruitId());
        redirectionIds.add(target.getShowcaseId());
        redirectionIds.add(target.getPeerlogId());
        return PortfolioDTO.builder()
                .teamId(other ? 0L : target.getTeamId())
                .tagList(new ArrayList<>())
                .teamName(target.getTeamName())
                .teamLogo(target.getTeamLogo())
                .recruitImage(target.getRecruitImage())
                .redirectionIds(redirectionIds)
                .end(last)
                .build();
    }

    @Transactional(readOnly = true)
    public List<PortfolioDTO> getMyPortfolioList(User user, Long page) {
        List<UserPortfolio> targetList = this.userPortfolioRepository.findByUserId(user.getId());
        if (targetList.isEmpty())
            return Collections.emptyList();
        List<PortfolioDTO> result = new ArrayList<>();
        long max = (6 * page);
        long initPoint = (max - 6);
        for (int i = (int) initPoint; i < max; i++) {
            if (targetList.size() > i + 1) {
                result.add(this.makePortfolioDTO(targetList.get(i), false, false));
            } else if (targetList.size() == i + 1) {
                result.add(this.makePortfolioDTO(targetList.get(i), true, false));
                break;
            } else {
                break ;
            }
        }

        List<Long> targetRecruitIds = new ArrayList<>();
        result.forEach(m -> targetRecruitIds.add(m.getRedirectionIds().get(0)));
        List<RecruitTag> tagIdList = this.recruitTagRepository.findByRecruitIdIn(targetRecruitIds);
        Set<Long> tagIds = new HashSet<>();
        tagIdList.forEach(m -> tagIds.add(m.getTagId()));
        List<SkillDTO> skillList = tagRepository.findSkillDTOByIdIn(new ArrayList<>(tagIds));

        for (PortfolioDTO element: result){
            Long recruitId = element.getRedirectionIds().get(0);
            tagIdList.forEach(recruitTag -> {
                if (recruitTag.getRecruitId().equals(recruitId)) {
                    skillList.forEach(t ->{
                        if (t.getTagId().equals(recruitTag.getTagId())){
                            element.getTagList().add(t);
                        }
                    });
                }
            });
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<PortfolioDTO> getOtherPortfolioList(Long otherId, Long page) {
        User targetUser = this.userRepository.findById(otherId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        if (!targetUser.isVisibilityForPortfolio())
        {
            return Collections.emptyList();
        }
        List<UserPortfolio> targetList = this.userPortfolioRepository.findByUserId(otherId);
        if (targetList.isEmpty())
            return Collections.emptyList();
        List<PortfolioDTO> result = new ArrayList<>();
        long max = (6 * page);
        long initPoint = (max - 6);
        for (int i = (int) initPoint; i < max; i++) {
            if (targetList.size() > i + 1) {
                result.add(this.makePortfolioDTO(targetList.get(i), false, true));
            } else if (targetList.size() == i + 1) {
                result.add(this.makePortfolioDTO(targetList.get(i), true, true));
                break;
            } else {
                break ;
            }
        }

        List<Long> targetRecruitIds = new ArrayList<>();
        result.forEach(m -> targetRecruitIds.add(m.getRedirectionIds().get(0)));
        List<RecruitTag> tagIdList = this.recruitTagRepository.findByRecruitIdIn(targetRecruitIds);
        Set<Long> tagIds = new HashSet<>();
        tagIdList.forEach(m -> tagIds.add(m.getTagId()));
        List<SkillDTO> skillList = tagRepository.findSkillDTOByIdIn(new ArrayList<>(tagIds));

        for (PortfolioDTO element: result){
            Long recruitId = element.getRedirectionIds().get(0);
            tagIdList.forEach(recruitTag -> {
                if (recruitTag.getRecruitId().equals(recruitId)) {
                    skillList.forEach(t ->{
                        if (t.getTagId().equals(recruitTag.getTagId())){
                            element.getTagList().add(t);
                        }
                    });
                }
            });
        }
        return result;
    }
}
