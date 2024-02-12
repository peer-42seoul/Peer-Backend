package peer.backend.service.board.recruit;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.RecruitWritingTracking;
import peer.backend.dto.board.recruit.*;
import peer.backend.dto.team.TeamApplyDataDTO;
import peer.backend.dto.team.TeamJobDto;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.enums.RecruitFavoriteEnum;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.composite.RecruitFavoritePK;
import peer.backend.entity.composite.TeamUserJobPK;
import peer.backend.entity.tag.RecruitTag;
import peer.backend.entity.tag.Tag;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamJob;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.TeamUserJob;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.team.enums.TeamUserStatus;
import peer.backend.entity.user.User;
import peer.backend.exception.IllegalArgumentException;
import peer.backend.exception.IndexOutOfBoundsException;
import peer.backend.exception.*;
import peer.backend.repository.board.recruit.RecruitFavoriteRepository;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.team.TeamJobRepository;
import peer.backend.repository.team.TeamUserJobRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.service.TagService;
import peer.backend.service.file.ObjectService;
import peer.backend.service.profile.UserPortfolioService;
import peer.backend.service.team.TeamService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final RecruitFavoriteRepository recruitFavoriteRepository;
    private final TeamService teamService;
    private final ObjectService objectService;
    private final TagService tagService;
    private final TeamUserRepository teamUserRepository;
    private final TeamJobRepository teamJobRepository;
    private final TeamUserJobRepository teamUserJobRepository;
    private final UserPortfolioService userPortfolioService;
    private final EntityManager entityManager;

    //query 생성 및 주입
    @PersistenceContext
    private EntityManager em;

    //Markdown에서 form-data를 추출하기 위한 패턴 ![](*)
    //TODO: toast에디터로 바뀔 경우 사용
//    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[\\]\\(data:image.*?\\)");


    public void changeRecruitFavorite(Authentication auth, Long recruitId,
        RecruitFavoriteEnum type) {
        User user = User.authenticationToUser(auth);
        if (!recruitRepository.existsById(recruitId)) {
            throw new NotFoundException("존재하지 않는 모집글입니다.");
        }
        recruitFavoriteRepository.findById(new RecruitFavoritePK(user.getId(), recruitId))
            .ifPresentOrElse(
                favorite -> {
                    if (favorite.getType().equals(type)) {
                        recruitFavoriteRepository.delete(favorite);
                    } else {
                        favorite.setType(type);
                        recruitFavoriteRepository.save(favorite);
                    }
                },
                () -> {
                    RecruitFavorite newFavorite = new RecruitFavorite();
                    newFavorite.setUserId(user.getId());
                    newFavorite.setRecruitId(recruitId);
                    newFavorite.setType(type);
                    recruitFavoriteRepository.save(newFavorite);
                });
    }

    public List<RecruitInterviewDto> getInterviewList(Long recruit_id) {
        Recruit recruit = recruitRepository.findById(recruit_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        List<RecruitInterviewDto> result = new ArrayList<>();
        for (RecruitInterview question : recruit.getInterviews()) {
            RecruitInterviewDto recruitInterviewDto = RecruitInterviewDto.builder()
                .question(question.getQuestion())
                .type(question.getType().toString())
                .optionList(question.getOptions())
                .build();
            result.add(recruitInterviewDto);
        }

        return result;
    }

    public List<Recruit> getRecruitListByCriteria(RecruitListRequest request) {
        //TODO:favorite 등
        //query 생성 준비
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Recruit> cq = cb.createQuery(Recruit.class).distinct(true);
        Root<Recruit> recruit = cq.from(Recruit.class);
        List<Predicate> predicates = new ArrayList<>();

        Join<Recruit, Team> teamJoin = recruit.join(
                "team"); // Assuming "team" is the name of the field in Recruit entity that references Team entity

        // query 생성
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            List<RecruitStatus> statuses = request.getStatus().stream()
                    .map(RecruitStatus::from)
                    .collect(Collectors.toList());
            predicates.add(recruit.get("status").in(statuses));
        }
        if (request.getTag() != null && !request.getTag().isEmpty()) {
            Join<Recruit, RecruitTag> recruitTagsJoin = recruit.join("recruitTags");
            Join<RecruitTag, Tag> tagJoin = recruitTagsJoin.join("tag");
            predicates.add(tagJoin.get("name").in(request.getTag()));
        }
        if (request.getType() != null && !request.getType().isEmpty()) {
            predicates.add(cb.equal(teamJoin.get("type"), TeamType.valueOf(request.getType())));
        }
        if (request.getPlace() != null && !request.getPlace().isEmpty()) {
            List<TeamOperationFormat> places = request.getPlace().stream()
                    .map(TeamOperationFormat::valueOf)
                    .collect(Collectors.toList());
            predicates.add(teamJoin.get("operationFormat").in(places));
        }
        if (request.getRegion1() != null && !request.getRegion1().isEmpty()) {
            predicates.add(cb.equal(teamJoin.get("region1"), request.getRegion1()));
        }
        if (request.getRegion2() != null && !request.getRegion2().isEmpty()) {
            predicates.add(cb.equal(teamJoin.get("region2"), request.getRegion2()));
        }
        if (request.getDue() != null && !request.getDue().isEmpty()) {
            predicates.add(
                    cb.between(teamJoin.get("dueValue"), request.getStart(), request.getEnd()));
        }
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            predicates.add(cb.like(recruit.get("title"), "%" + request.getKeyword() + "%"));
        }
        //sort 기준 설정
        List<Order> orders = new ArrayList<>();
        switch (request.getSort()) {
            case "latest":
                orders.add(cb.desc(recruit.get("createdAt")));
                break;
            case "hit":
                orders.add(cb.desc(recruit.get("hit")));
                break;
            default:
                throw new IllegalArgumentException("Invalid sort value");
        }
        //query 전송
// order 적용
        cq.orderBy(orders);

// Predicate 적용
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

// 쿼리 실행 부분
        TypedQuery<Recruit> query = em.createQuery(cq);
        return query.getResultList();
    }

    public Page<RecruitListResponse> getRecruitSearchList(Pageable pageable,
        RecruitListRequest request, User user) {

        List<Recruit> recruits = getRecruitListByCriteria(request);

        List<RecruitListResponse> results = recruits.stream()
            .map(recruit2 -> new RecruitListResponse(
                recruit2.getTitle(),
                recruit2.getThumbnailUrl(),
                recruit2.getWriterId(),
                recruit2.getWriter() == null ? null : recruit2.getWriter().getNickname(),
                recruit2.getWriter() == null ? null : recruit2.getWriter().getImageUrl(),
                recruit2.getStatus().toString(),
                // TODO:  맞나 성능 개선이 필요한거 같기도
                this.tagService.recruitTagListToTagResponseList(recruit2.getRecruitTags()),
                recruit2.getId(),
        user != null && recruitFavoriteRepository
                                .existsByUserIdAndRecruitIdAndType(user.getId(), recruit2.getId(), RecruitFavoriteEnum.LIKE),
                recruit2.getUpdatedAt().toString())
            ).collect(Collectors.toList());

        int fromIndex = pageable.getPageNumber() * pageable.getPageSize();
        if (fromIndex > results.size()) {
            throw new IndexOutOfBoundsException("존재하지 않는 페이지입니다");
        }
        return new PageImpl<>(results.subList(fromIndex,
            Math.min(fromIndex + pageable.getPageSize(), results.size())), pageable,
            results.size());
    }

    @Transactional
    public RecruitResponce getRecruit(Long recruit_id, Authentication auth) {
        Recruit recruit = recruitRepository.findById(recruit_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        List<TeamJob> teamJobs = recruit.getTeam().getJobs();
        recruit.setHit(recruit.getHit() + 1);
        List<TeamJobDto> jobDtoList = new ArrayList<>();
        teamJobs.forEach(
            role -> jobDtoList.add(
                new TeamJobDto(
                    role.getName(),
                    role.getMax(),
                    role.getCurrent())));
        Team team = recruit.getTeam();
        return RecruitResponce.builder()
            .title(recruit.getTitle())
            .content(recruit.getContent())
            .region((Objects.isNull(team.getRecruit()) || Objects.isNull(team.getRegion2()) ? null
                : new ArrayList<>(List.of(team.getRegion1(), team.getRegion2()))))
            .status(recruit.getStatus())
            .totalNumber(team.getJobs().stream().mapToInt(TeamJob::getMax).sum())
            .current(teamUserJobRepository.findByTeamUserTeamIdAndStatus(team.getId(),
                TeamUserStatus.APPROVED).size())
            .due(team.getDueTo().getLabel())
            .link(recruit.getLink())
            .leader_id(recruit.getWriterId())
            .leader_nickname(recruit.getWriter() == null ? null : recruit.getWriter().getNickname())
            .leader_image(recruit.getWriter() == null ? null : recruit.getWriter().getImageUrl())
            .tagList(this.tagService.recruitTagListToTagResponseList(recruit.getRecruitTags()))
            .roleList(jobDtoList)
            .place(team.getOperationFormat())
            .image(recruit.getThumbnailUrl())
            .teamName(recruit.getTeam().getName())
            .isFavorite((auth != null) &&
                recruitFavoriteRepository.existsByUserIdAndRecruitIdAndType(
                    User.authenticationToUser(auth).getId(),
                    recruit_id,
                    RecruitFavoriteEnum.LIKE)
            )
            .updatedAt(recruit.getUpdatedAt().toString())
            .build();
    }

    public RecruitUpdateResponse getRecruitwithInterviewList(Long recruit_id) {
        Recruit recruit = recruitRepository.findById(recruit_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        List<TeamJob> teamJobs = recruit.getTeam().getJobs();
        List<TeamJobDto> roleDtoList = new ArrayList<>();
        teamJobs.forEach(
            role -> roleDtoList.add(
                new TeamJobDto(role.getName(), role.getMax(), role.getCurrent())));
        Team team = recruit.getTeam();
        //TODO:DTO 항목 추가 필요
        return RecruitUpdateResponse.builder()
            .title(recruit.getTitle())
            .content(recruit.getContent())
            .region1(team.getRegion1())
            .region2(team.getRegion2())
            .status(recruit.getStatus())
            .totalNumber(teamJobs.stream().mapToInt(TeamJob::getMax).sum())
            .current(team.getTeamUsers().size())
            .due(team.getDueTo().getLabel())
            .link(recruit.getLink())
            .leader_id(recruit.getWriterId())
            .leader_nickname(
                Objects.isNull(recruit.getWriter()) ? null : recruit.getWriter().getNickname())
            .leader_image(
                Objects.isNull(recruit.getWriter()) ? null : recruit.getWriter().getImageUrl())
            .tagList(this.tagService.recruitTagListToTagResponseList(recruit.getRecruitTags()))
            .roleList(roleDtoList)
            .interviewList(getInterviewList(recruit_id))
            .isAnswered(recruit.getTeam().getTeamUsers().size() > 1)
            .place(team.getOperationFormat().getValue())
            .type(team.getType().getValue())
            .name(recruit.getTeam().getName())
            .image(recruit.getThumbnailUrl())
            .build();
    }

    private void addInterviewsToRecruit(Recruit recruit, List<RecruitInterviewDto> interviewList) {
        if (interviewList != null && !interviewList.isEmpty()) {
            for (RecruitInterviewDto interview : interviewList) {
                recruit.addInterview(interview);
            }
        }
    }

    private Recruit createRecruitFromDto(RecruitCreateRequest request, Team team, User user) {
        Recruit recruit = Recruit.builder()
            .team(team)
            .title(request.getTitle())
            .link(request.getLink())
            .content(request.getContent())
            .status(RecruitStatus.ONGOING)
            .thumbnailUrl(
                objectService.uploadObject("recruit/" + team.getId().toString(), request.getImage(),
                    "image"))
            .writerId(user.getId())
            .writer(user)
            .hit(0L)
            .build();
        addInterviewsToRecruit(recruit, request.getInterviewList());
        return recruit;
    }

    @Transactional
    @RecruitWritingTracking
    public Recruit createRecruit(RecruitCreateRequest request, Authentication auth) {
        User user = User.authenticationToUser(auth);
        //팀 생성
        Team team = this.teamService.createTeam(user, request);

        //모집게시글 생성
        Recruit recruit = recruitRepository.save(createRecruitFromDto(request, team, user));
        if (request.getTagList() != null) {
            recruit.setRecruitTags(request.getTagList().stream()
                .map(e -> (new RecruitTag(recruit.getId(), e))).collect(
                    Collectors.toList()));
        }
        recruit.addFiles(objectService.extractContentImage(request.getContent()));
        return recruit;
    }

    @Transactional
    public void applyRecruit(Long recruit_id, ApplyRecruitRequest request, Authentication auth) {
        // 모집글 찾기
        Recruit recruit = recruitRepository.findById(recruit_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));


        // 모집글 작성자 여부 검증
        User user = User.authenticationToUser(auth);
        if (user.getId().equals(recruit.getWriterId())) {
            throw new BadRequestException("모집글 작성자는 팀에 지원할 수 없습니다.");
        }
        Team team = recruit.getTeam();

        // 모집 완료 경우 검증
        if (recruit.getStatus().equals(RecruitStatus.DONE)) {
            throw new BadRequestException("모집이 완료된 모집글에서는 지원할 수가 없습니다.");
        }

        // 지원 역할 검증
        TeamJob teamJob = teamJobRepository.findByTeamIdAndName(recruit_id, request.getRole())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 역할입니다."));

        String query = "SELECT new peer.backend.dto.team.TeamApplyDataDTO(" +
                "tj.id, tj.name, tj.max, tj.team.id, " +
                "(SELECT COUNT(tu) FROM TeamUserJob tu WHERE tu.teamJobId = tj.id AND tu.status = 'PENDING'), " +
                "(SELECT COUNT(tv) FROM TeamUserJob tv WHERE tv.teamJobId = tj.id AND tv.status = 'APPROVED')) " +
                " FROM TeamJob tj " +
                " WHERE tj.team.id = :teamId AND tj.name != 'Leader'";

        List<TeamApplyDataDTO> teamData = this.entityManager.createQuery(query, TeamApplyDataDTO.class).setParameter("teamId", team.getId()).getResultList();
        teamData.forEach(m -> {
            if (m.getName().equals(request.getRole())) {
                if (m.getMax() - m.getApplyNumber() == 0)
                    throw new BadRequestException("지원이 불가능합니다!");
            }
        });

        // 팀 지원자 리스트 확인
        TeamUser teamUser = this.teamUserRepository.findByUserIdAndTeamId(user.getId(),
            team.getId()).orElse(null);
        if (Objects.isNull(teamUser)) {
            teamUser = TeamUser.builder()
                .teamId(team.getId())
                .userId(user.getId())
                .role(TeamUserRoleType.MEMBER)
                .status(TeamUserStatus.PENDING)
                .build();
            teamUserRepository.save(teamUser);
        } else if (teamUserJobRepository.existsById(
            new TeamUserJobPK(teamUser.getId(), teamJob.getId()))) {
            // 이미 지원한 경우
            throw new ConflictException("이미 지원하였습니다.");
        }

        // 팀 유저에 추가
        teamUser.addTeamUserJob(TeamUserJob.builder()
            .teamJobId(teamJob.getId())
            .teamUserId(teamUser.getId())
            .status(TeamUserStatus.PENDING)
            .answers(request.getAnswerList())
            .build());
    }

    @Transactional
    public void deleteRecruit(Long recruit_id) {
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(
            () -> new NotFoundException("존재하지 않는 모집게시글입니다."));
        if (recruit.getStatus().equals(RecruitStatus.DONE)) {
            throw new BadRequestException("모집이 완료된 게시글은 삭제할 수 없습니다.");
        }
        if (teamUserRepository.existsApprovedByTeamId(recruit.getTeam().getId())) {
            throw new BadRequestException("승인된 팀원이 있는 팀의 모집글은 삭제할 수 없습니다.");
        }

        objectService.deleteObject(recruit.getThumbnailUrl());
        if (recruit.getFiles() != null && !recruit.getFiles().isEmpty())
            recruit.getFiles().forEach(file -> objectService.deleteObject(file.getUrl()));
        recruitRepository.delete(recruit);
    }

    @Transactional
    public Long updateRecruit(Long recruit_id, RecruitUpdateRequestDTO recruitUpdateRequestDTO) {
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(
            () -> new NotFoundException("존재하지 않는 모집게시글입니다."));
        List<String> contentImages = objectService.extractContentImage(recruitUpdateRequestDTO.getContent());
        if (recruitUpdateRequestDTO.getImage() != null) {
            recruit.update(recruitUpdateRequestDTO, contentImages);
            objectService.deleteObject(recruit.getThumbnailUrl());
            recruit.setThumbnailUrl(objectService.uploadObject(recruitUpdateRequestDTO.getImage(),
                "recruit/" + recruit_id, "image"));
            this.userPortfolioService.setRecruitImagePath(recruit.getId(), recruit.getThumbnailUrl());
        } else {
            recruit.update(recruitUpdateRequestDTO, contentImages);
        }
        return recruit.getId();
    }

    @Transactional
    public List<RecruitFavoriteResponse> getFavoriteList(RecruitListRequest request, User user)
    {
        List<Recruit> recruitList = getRecruitListByCriteria(request);

        return recruitList.stream()
                .map(recruit -> RecruitFavoriteResponse.builder()
                        .recruit_id(recruit.getId())
                        .favorite(user != null && (recruitFavoriteRepository.existsByUserIdAndRecruitIdAndType(
                                    user.getId(),
                                    recruit.getId(),
                                    RecruitFavoriteEnum.LIKE)))
                        .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    public boolean getFavorite(Long recruit_id, User user) {
        if (!recruitRepository.existsById(recruit_id))
            throw new NotFoundException("존재하지 않는 게시글입니다.");
        return (user != null
                && recruitFavoriteRepository
                .existsByUserIdAndRecruitIdAndType(
                        user.getId(), recruit_id, RecruitFavoriteEnum.LIKE)
        );
    }
}
