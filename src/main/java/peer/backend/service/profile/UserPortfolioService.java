package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamUserStatus;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserPortfolio;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.user.UserPortfolioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPortfolioService {

    private final UserPortfolioRepository userPortfolioRepository;
    private final TeamRepository teamRepository;

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

}
