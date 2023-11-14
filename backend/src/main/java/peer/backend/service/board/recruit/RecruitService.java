package peer.backend.service.board.recruit;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.apache.commons.math3.exception.OutOfRangeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.dto.board.recruit.ApplyRecruitRequest;
import peer.backend.dto.board.recruit.RecruitAnswerDto;
import peer.backend.dto.board.recruit.RecruitCreateRequest;
import peer.backend.dto.board.recruit.RecruitInterviewDto;
import peer.backend.dto.board.recruit.RecruitListRequest;
import peer.backend.dto.board.recruit.RecruitListResponse;
import peer.backend.dto.board.recruit.RecruitResponce;
import peer.backend.dto.board.recruit.RecruitRoleDTO;
import peer.backend.dto.board.recruit.RecruitUpdateRequestDTO;
import peer.backend.dto.board.recruit.RecruitUpdateResponse;
import peer.backend.dto.team.TeamApplicantListDto;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitApplicant;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;
import peer.backend.entity.board.recruit.Tag;
import peer.backend.entity.board.recruit.TagListManager;
import peer.backend.entity.board.recruit.enums.RecruitApplicantStatus;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.composite.RecruitApplicantPK;
import peer.backend.entity.composite.RecruitFavoritePK;
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
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.file.FileService;
import peer.backend.service.team.TeamService;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final UserRepository userRepository;
    private final RecruitRepository recruitRepository;
    private final TeamRepository teamRepository;
    private final RecruitFavoriteRepository recruitFavoriteRepository;
    private final RecruitApplicantRepository recruitApplicantRepository;
    private final TeamUserRepository teamUserRepository;
    private final FileService fileService;
    private final TeamService teamService;

    //query 생성 및 주입
    @PersistenceContext
    private EntityManager em;

    private List<Tag> preDefinedTagList = TagListManager.getPredefinedTags();

    //Markdown에서 form-data를 추출하기 위한 패턴 ![](*)
    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[\\]\\(data:image.*?\\)");


    public void changeRecruitFavorite(Long user_id, Long recruit_id) {
        User user = userRepository.findById(user_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        Recruit recruit = recruitRepository.findById(recruit_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        Optional<RecruitFavorite> optFavorite = recruitFavoriteRepository.findById(
            new RecruitFavoritePK(user_id, recruit_id));
        if (optFavorite.isPresent()) {
            recruitFavoriteRepository.delete(optFavorite.get());
        } else {
            RecruitFavorite favorite = new RecruitFavorite();
            favorite.setUser(user);
            favorite.setRecruit(recruit);
            favorite.setUserId(user_id);
            favorite.setRecruitId(recruit_id);
            recruitFavoriteRepository.save(favorite);
        }
    }

    public List<RecruitInterviewDto> getInterviewList(Long recruit_id) {
        Recruit recruit = recruitRepository.findById(recruit_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        List<RecruitInterviewDto> result = new ArrayList<>();
        for (RecruitInterview question : recruit.getInterviews()) {
            RecruitInterviewDto recruitInterviewDto = RecruitInterviewDto.builder()
                .question(question.getQuestion())
                .type(question.getType())
                .optionList(question.getOptions())
                .build();
            result.add(recruitInterviewDto);
        }

        return result;
    }

    public List<TeamApplicantListDto> getTeamApplicantList(Long user_id) {
        //TODO:모듈화 리팩토링 필요
        User user = userRepository.findById(user_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        List<RecruitApplicant> recruitApplicantList = recruitApplicantRepository.findByUserId(
            user_id);
        List<TeamApplicantListDto> result = new ArrayList<>();

        //questionList 이터레이트 하면서 dtoList만들기
        for (RecruitApplicant recruitApplicant : recruitApplicantList) {
            ArrayList<RecruitAnswerDto> answerDtoList = new ArrayList<>();
            List<String> answerList = recruitApplicant.getAnswerList();
            List<RecruitInterview> questionList = recruitApplicant.getRecruit().getInterviews();
            int index = 0;
            for (RecruitInterview question : questionList) {
                RecruitAnswerDto answerDto = RecruitAnswerDto.builder()
                    .question(question.getQuestion())
                    .answer(answerList.get(index))
                    .type(question.getType().toString())
                    .option(question.getOptions())
                    .build();
                index++;
                answerDtoList.add(answerDto);
            }
            result.add(TeamApplicantListDto.builder()
                .answers(answerDtoList)
                .name(user.getNickname())
                .userId(recruitApplicant.getRecruitId())
                .build());
        }
        return result;
    }

    public Page<RecruitListResponse> getRecruitSearchList(Pageable pageable,
        RecruitListRequest request, Authentication auth) {
        //TODO:favorite 등
        //query 생성 준비
        String[] dues = {"1주일", "2주일", "3주일", "1달", "2달", "3달"};

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
        if (request.getType() != null && !request.getType().isEmpty()){
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
            predicates.add(cb.equal(recruit.get("due"), request.getDue()));
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
// Pageable 적용
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
                        recruit2.getWriter().getNickname(),
                        recruit2.getWriter().getImageUrl(),
                        recruit2.getStatus().toString(),
                        TagListManager.getRecruitTags(recruit2.getTags()),
                        recruit2.getId(),
                        ((auth != null) &&
                                (recruitFavoriteRepository
                                        .findById(new RecruitFavoritePK(User.authenticationToUser(auth).getId(), recruit2.getId()))
                                        .isPresent()))))
                .collect(Collectors.toList());

        int fromIndex = pageable.getPageNumber() * pageable.getPageSize();
        if (fromIndex > results.size())
                throw new IndexOutOfBoundsException("존재하지 않는 페이지입니다");
        return  new PageImpl<>(results.subList(fromIndex, Math.min(fromIndex + pageable.getPageSize(), results.size())), pageable, results.size());
    }

    @Transactional
    public RecruitResponce getRecruit(Long recruit_id) {
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
            .due(recruit.getDue())
            .link(recruit.getLink())
            .leader_id(recruit.getWriter().getId())
            .leader_nickname(recruit.getWriter().getNickname())
            .leader_image(recruit.getWriter().getImageUrl())
            .tagList(TagListManager.getRecruitTags(recruit.getTags()))
            .roleList(roleDtoList)
            .place(recruit.getPlace())
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
            .due(recruit.getDue())
            .link(recruit.getLink())
            .leader_id(recruit.getWriter().getId())
            .leader_nickname(recruit.getWriter().getNickname())
            .leader_image(recruit.getWriter().getImageUrl())
            .tagList(TagListManager.getRecruitTags(recruit.getTags()))
            .roleList(roleDtoList)
            .interviewList(getInterviewList(recruit_id))
            .build();
    }

    private void addInterviewsToRecruit(Recruit recruit, List<RecruitInterview> interviewList) {
        if (interviewList != null && !interviewList.isEmpty()) {
            for (RecruitInterview interview : interviewList) {
                recruit.addInterview(interview);
            }
        }
    }

    private void addRolesToRecruit(Recruit recruit, List<RecruitRole> roleList) {
        if (roleList != null && !roleList.isEmpty()) {
            for (RecruitRole role : roleList) {
                recruit.addRole(role);
            }
        }
    }

    private List<String> processMarkdownWithFormData(String markdown) throws IOException {
        //TODO:Storage에 맞춰 filePath 수정, fileType검사, file 모듈로 리팩토링, fileList에 추가
        Matcher matcher = IMAGE_PATTERN.matcher(markdown);
        StringBuilder sb = new StringBuilder();
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            String formData = matcher.group().substring(26, matcher.group().length() - 1);
            byte[] imageBytes = Base64.getDecoder().decode(formData);
            Path path = Paths.get("/Users/jwee/upload", UUID.randomUUID() + ".png");
            Files.write(path, imageBytes);
            if (result.isEmpty()) {
                result.add(path.toString());
            }
            matcher.appendReplacement(sb, "![image](" + path + ")");
        }
        if (result.isEmpty()) {
            result.add("");
        }
        matcher.appendTail(sb);
        result.add(sb.toString());
        return result;
    }

    private Recruit createRecruitFromDto(MultipartFile image, RecruitCreateRequest request,
        Team team, User user) throws IOException {
        List<String> content = processMarkdownWithFormData(request.getContent());
        Recruit recruit = Recruit.builder()
            .team(team)
            .type(TeamType.valueOf(request.getType()))
            .title(request.getTitle())
            .due(request.getDue())
            .link(request.getLink())
            .content(content.get(1))
            .place(TeamOperationFormat.valueOf(request.getPlace()))
            .region1(request.getPlace().equals("온라인") ? null : request.getRegion().get(0))
            .region2(request.getPlace().equals("온라인") ? null : request.getRegion().get(1))
            .tags(request.getTagList())
            .status(RecruitStatus.ONGOING)
            .thumbnailUrl((content.get(0).isBlank()) ? null : content.get(0))
            .writerId(user.getId())
            .writer(user)
            .hit(0L)
            .thumbnailUrl(fileService.saveFile(image, "/Users/jwee", "image"))
            .build();
        //List 추가
        addInterviewsToRecruit(recruit, request.getInterviewList());
        addRolesToRecruit(recruit, request.getRoleList());
        return recruit;
    }


    @Transactional
    public void createRecruit(MultipartFile image, RecruitCreateRequest request,
        Authentication auth) throws IOException {
        //동일한 팀 이름 검사
        Optional<Team> findTeam = teamRepository.findByName(request.getName());
        if (findTeam.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 팀 이름입니다.");
        }
        User user = User.authenticationToUser(auth);
        //팀 생성
        Team team = this.teamService.createTeam(user, request);

        //모집게시글 생성
        Recruit recruit = createRecruitFromDto(image, request, team, user);
        recruitRepository.save(recruit);
    }

    @Transactional
    public void applyRecruit(Long recruit_id, ApplyRecruitRequest request, Authentication auth) {
        Recruit recruit = recruitRepository.findById(recruit_id)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        User user = User.authenticationToUser(auth);
        Optional<RecruitApplicant> optRecruitApplicant = recruitApplicantRepository.findById(
            new RecruitApplicantPK(recruit_id, user.getId()));

        if (optRecruitApplicant.isPresent()) {
            throw new ConflictException("이미 지원한 팀입니다.");
        }
        RecruitApplicant recruitApplicant = RecruitApplicant.builder()
            .recruitId(recruit_id)
            .userId(user.getId())
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
        recruitRepository.delete(recruit);
    }

    @Transactional
    public void updateRecruit(Long recruit_id, RecruitUpdateRequestDTO recruitUpdateRequestDTO)
        throws IOException {
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(
            () -> new NotFoundException("존재하지 않는 모집게시글입니다."));

        List<String> content = processMarkdownWithFormData(recruitUpdateRequestDTO.getContent());
        recruit.update(recruitUpdateRequestDTO, content);
    }

    public List<Tag> getTagList() {
        return preDefinedTagList;
    }
}
