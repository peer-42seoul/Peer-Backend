package peer.backend.service.board.recruit;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.RecruitWritingTracking;
import peer.backend.dto.board.recruit.ApplyRecruitRequest;
import peer.backend.dto.board.recruit.RecruitCreateRequest;
import peer.backend.dto.board.recruit.RecruitInterviewDto;
import peer.backend.dto.board.recruit.RecruitListRequest;
import peer.backend.dto.board.recruit.RecruitListResponse;
import peer.backend.dto.board.recruit.RecruitResponce;
import peer.backend.dto.board.recruit.RecruitRoleDTO;
import peer.backend.dto.board.recruit.RecruitUpdateRequestDTO;
import peer.backend.dto.board.recruit.RecruitUpdateResponse;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitApplicant;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;
import peer.backend.entity.board.recruit.Tag;
import peer.backend.entity.board.recruit.TagListManager;
import peer.backend.entity.board.recruit.enums.RecruitApplicantStatus;
import peer.backend.entity.board.recruit.enums.RecruitDueEnum;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.composite.RecruitApplicantPK;
import peer.backend.entity.composite.RecruitFavoritePK;
import peer.backend.entity.tag.RecruitTag;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.IllegalArgumentException;
import peer.backend.exception.IndexOutOfBoundsException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.recruit.RecruitApplicantRepository;
import peer.backend.repository.board.recruit.RecruitFavoriteRepository;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.service.TagService;
import peer.backend.service.file.ObjectService;
import peer.backend.service.team.TeamService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final TeamRepository teamRepository;
    private final RecruitFavoriteRepository recruitFavoriteRepository;
    private final RecruitApplicantRepository recruitApplicantRepository;
    private final TeamService teamService;
    private final ObjectService objectService;
    private final TagService tagService;

    //query 생성 및 주입
    @PersistenceContext
    private EntityManager em;

    private final List<Tag> preDefinedTagList = TagListManager.getPredefinedTags();

    //Markdown에서 form-data를 추출하기 위한 패턴 ![](*)
    //TODO: toast에디터로 바뀔 경우 사용
//    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[\\]\\(data:image.*?\\)");


    public void changeRecruitFavorite(Authentication auth, Long recruit_id) {
        User user = User.authenticationToUser(auth);
        Recruit recruit = recruitRepository.findById(recruit_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        recruitFavoriteRepository.findById(new RecruitFavoritePK(user.getId(), recruit_id))
            .ifPresentOrElse(recruitFavoriteRepository::delete,
                () -> {
                    RecruitFavorite newFavorite = new RecruitFavorite();
                    newFavorite.setUser(user);
                    newFavorite.setRecruit(recruit);
                    newFavorite.setUserId(user.getId());
                    newFavorite.setRecruitId(recruit_id);
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

    public Page<RecruitListResponse> getRecruitSearchList(Pageable pageable,
        RecruitListRequest request, Authentication auth) {
        //TODO:favorite 등
        //query 생성 준비
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Recruit> cq = cb.createQuery(Recruit.class).distinct(true);
        Root<Recruit> recruit = cq.from(Recruit.class);
        List<Predicate> predicates = new ArrayList<>();

        // query 생성
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            List<RecruitStatus> statuses = request.getStatus().stream()
                .map(RecruitStatus::from)
                .collect(Collectors.toList());
            predicates.add(recruit.get("status").in(statuses));
        }
        if (request.getTag() != null && !request.getTag().isEmpty()) {
            Join<Recruit, String> tagList = recruit.join("tags");
            predicates.add(tagList.in(request.getTag()));
        }
        if (request.getType() != null && !request.getType().isEmpty()) {
            predicates.add(cb.equal(recruit.get("type"), TeamType.valueOf(request.getType())));
        }
        if (request.getPlace() != null && !request.getPlace().isEmpty()) {
            List<TeamOperationFormat> places = request.getPlace().stream()
                .map(TeamOperationFormat::valueOf)
                .collect(Collectors.toList());
            predicates.add(recruit.get("place").in(places));
        }
        if (request.getRegion1() != null && !request.getRegion1().isEmpty()) {
            predicates.add(cb.equal(recruit.get("region1"), request.getRegion1()));
        }
        if (request.getRegion2() != null && !request.getRegion2().isEmpty()) {
            predicates.add(cb.equal(recruit.get("region2"), request.getRegion2()));
        }
        if (request.getDue() != null && !request.getDue().isEmpty()) {
            predicates.add(
                cb.between(recruit.get("dueValue"), request.getStart(), request.getEnd()));
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
        List<Recruit> recruits = query.getResultList();

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
//                recruit2.getRecruitTags().stream().map(RecruitTag::getTag)
//                    .collect(Collectors.toList())
//                    .stream().map(
//                        TagResponse::new).collect(Collectors.toList()),
//                TagListManager.getRecruitTags(recruit2.getTags()),
                recruit2.getId(),
                ((auth != null) &&
                    (recruitFavoriteRepository
                        .findById(new RecruitFavoritePK(User.authenticationToUser(auth).getId(),
                            recruit2.getId()))
                        .isPresent()))))
            .collect(Collectors.toList());

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
        recruit.setHit(recruit.getHit() + 1);
        List<RecruitRoleDTO> roleDtoList = new ArrayList<>();
        for (RecruitRole role : recruit.getRoles()) {
            roleDtoList.add(new RecruitRoleDTO(role.getName(), role.getNumber()));
        }
        return RecruitResponce.builder()
            .title(recruit.getTitle())
            .content(recruit.getContent())
            .region(new ArrayList<>(List.of(recruit.getRegion1(), recruit.getRegion2())))
            .status(recruit.getStatus())
            .totalNumber(recruit.getRoles().size())
            .due(recruit.getDue().getLabel())
            .link(recruit.getLink())
            .leader_id(recruit.getWriterId())
            .leader_nickname(recruit.getWriter() == null ? null : recruit.getWriter().getNickname())
            .leader_image(recruit.getWriter() == null ? null : recruit.getWriter().getImageUrl())
//            .tagList(TagListManager.getRecruitTags(recruit.getTags()))
            .tagList(this.tagService.recruitTagListToTagResponseList(recruit.getRecruitTags()))
            .roleList(roleDtoList)
            .place(recruit.getPlace())
            .image(recruit.getThumbnailUrl())
            .teamName(recruit.getTeam().getName())
            .isFavorite((auth != null) && recruitFavoriteRepository.findById(
                    new RecruitFavoritePK(User.authenticationToUser(auth).getId(), recruit_id))
                .isPresent())
            .build();
    }

    public RecruitUpdateResponse getRecruitwithInterviewList(Long recruit_id) {
        Recruit recruit = recruitRepository.findById(recruit_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        List<RecruitRoleDTO> roleDtoList = new ArrayList<>();
        for (RecruitRole role : recruit.getRoles()) {
            roleDtoList.add(new RecruitRoleDTO(role.getName(), role.getNumber()));
        }
        //TODO:DTO 항목 추가 필요
        return RecruitUpdateResponse.builder()
            .title(recruit.getTitle())
            .content(recruit.getContent())
            .region1(recruit.getRegion1())
            .region2(recruit.getRegion2())
            .status(recruit.getStatus())
            .totalNumber(recruit.getRoles().size())
            .due(recruit.getDue().getLabel())
            .link(recruit.getLink())
            .leader_id(recruit.getWriter().getId())
            .leader_nickname(recruit.getWriter() == null ? null : recruit.getWriter().getNickname())
            .leader_image(recruit.getWriter() == null ? null : recruit.getWriter().getImageUrl())
//            .tagList(TagListManager.getRecruitTags(recruit.getRecruitTags()))
            .tagList(this.tagService.recruitTagListToTagResponseList(recruit.getRecruitTags()))
            .roleList(roleDtoList)
            .interviewList(getInterviewList(recruit_id))
            .isAnswered(!recruit.getApplicants().isEmpty())
            .build();
    }

    private void addInterviewsToRecruit(Recruit recruit, List<RecruitInterviewDto> interviewList) {
        if (interviewList != null && !interviewList.isEmpty()) {
            for (RecruitInterviewDto interview : interviewList) {
                recruit.addInterview(interview);
            }
        }
    }

    private void addRolesToRecruit(Recruit recruit, List<RecruitRoleDTO> roleList) {
        if (roleList != null && !roleList.isEmpty()) {
            for (RecruitRoleDTO role : roleList) {
                recruit.addRole(role);
            }
        }
    }

// TODO: 2스텝에서 에디터 변경시 적용 필요
//    private List<String> processMarkdownWithFormData(String markdown) throws IOException {
//        //TODO:Storage에 맞춰 filePath 수정, fileType검사, file 모듈로 리팩토링, fileList에 추가
//        Matcher matcher = IMAGE_PATTERN.matcher(markdown);
//        StringBuilder sb = new StringBuilder();
//        List<String> result = new ArrayList<>();
//        while (matcher.find()) {
//            String formData = matcher.group().substring(26, matcher.group().length() - 1);
//            byte[] imageBytes = Base64.getDecoder().decode(formData);
//            Path path = Paths.get("/Users/jwee/upload", UUID.randomUUID() + ".png");
//            Files.write(path, imageBytes);
//            if (result.isEmpty()) {
//                result.add(path.toString());
//            }
//            matcher.appendReplacement(sb, "![image](" + path + ")");
//        }
//        if (result.isEmpty()) {
//            result.add("");
//        }
//        matcher.appendTail(sb);
//        result.add(sb.toString());
//        return result;
//    }

    private Recruit createRecruitFromDto(RecruitCreateRequest request, Team team, User user) {
        Recruit recruit = Recruit.builder()
            .team(team)
            .type(TeamType.valueOf(request.getType()))
            .title(request.getTitle())
            .due(RecruitDueEnum.from(request.getDue()))
            .link(request.getLink())
            .content(request.getContent())
            .place(TeamOperationFormat.valueOf(request.getPlace()))
            .region1(request.getPlace().equals("온라인") ? null : request.getRegion().get(0))
            .region2(request.getPlace().equals("온라인") ? null : request.getRegion().get(1))
//            .tags(request.getTagList().stream().map(TagListResponse::getName)
//                .collect(Collectors.toList()))
            .status(RecruitStatus.ONGOING)
            .thumbnailUrl(
                objectService.uploadObject("recruit/" + team.getId().toString(), request.getImage(),
                    "image"))
            .writerId(user.getId())
            .writer(user)
            .hit(0L)
            .dueValue(10)
            .build();
        //List 추가
        addInterviewsToRecruit(recruit, request.getInterviewList());
        addRolesToRecruit(recruit, request.getRoleList());
        return recruit;
    }


    @Transactional
    @RecruitWritingTracking
    public String createRecruit(RecruitCreateRequest request, Authentication auth) {
        User user = User.authenticationToUser(auth);
        //동일한 팀 이름 검사
        teamRepository.findByName(request.getName()).ifPresent(
            team1 -> {
                throw new IllegalArgumentException("이미 존재하는 팀 이름입니다.");
            }
        );

        //팀 생성
        Team team = this.teamService.createTeam(user, request);

        //모집게시글 생성
        Recruit recruit = recruitRepository.save(createRecruitFromDto(request, team, user));
        recruit.setRecruitTags(request.getTagList().stream()
            .map(e -> (new RecruitTag(recruit, this.tagService.getTag(e)))).collect(
                Collectors.toList()));
        return recruit.getId().toString();
    }

    @Transactional
    public void applyRecruit(Long recruit_id, ApplyRecruitRequest request, Authentication auth) {
        Recruit recruit = recruitRepository.findById(recruit_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        User user = User.authenticationToUser(auth);
        Optional<RecruitApplicant> optRecruitApplicant = recruitApplicantRepository.findById(
            new RecruitApplicantPK(user.getId(), recruit_id,
                (request.getRole() == null ? "" : request.getRole())));
        if (optRecruitApplicant.isPresent()) {
            throw new ConflictException("이미 지원한 팀입니다.");
        }
        RecruitApplicant recruitApplicant = RecruitApplicant.builder()
            .recruitId(recruit_id)
            .userId(user.getId())
            .role(request.getRole() == null ? "" : request.getRole())
            .nickname(user.getNickname())
            .status(RecruitApplicantStatus.PENDING)
            .answerList(request.getAnswerList())
            .build();
        recruitApplicantRepository.save(recruitApplicant);
    }

    @Transactional
    public void deleteRecruit(Long recruit_id) {
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(
            () -> new NotFoundException("존재하지 않는 모집게시글입니다."));
        objectService.deleteObject(recruit.getThumbnailUrl());
        recruitRepository.delete(recruit);
    }

    @Transactional
    public void updateRecruit(Long recruit_id, RecruitUpdateRequestDTO recruitUpdateRequestDTO) {
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(
            () -> new NotFoundException("존재하지 않는 모집게시글입니다."));
        if (recruitUpdateRequestDTO.getImage() != null) {
            objectService.deleteObject(recruit.getThumbnailUrl());
            recruit.update(recruitUpdateRequestDTO,
                objectService.uploadObject(recruitUpdateRequestDTO.getImage(),
                    "recruit/" + recruit_id, "image"));
        }
    }

    public List<Tag> getTagList() {
        return preDefinedTagList;
    }
}
