package peer.backend.service.board.recruit;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import peer.backend.dto.board.recruit.*;
import peer.backend.dto.team.TeamApplicantListDto;
import peer.backend.entity.board.recruit.*;
import peer.backend.entity.board.recruit.enums.RecruitApplicantStatus;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.composite.RecruitApplicantPK;
import peer.backend.entity.composite.RecruitFavoritePK;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.*;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.IllegalArgumentException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.recruit.RecruitApplicantRepository;
import peer.backend.repository.board.recruit.RecruitFavoriteRepository;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitService {
    private final UserRepository userRepository;
    private final RecruitRepository recruitRepository;
    private final TeamRepository teamRepository;
    private final RecruitFavoriteRepository recruitFavoriteRepository;
    private final RecruitApplicantRepository recruitApplicantRepository;
    private final TeamUserRepository teamUserRepository;

    //query 생성 및 주입
    @PersistenceContext
    private EntityManager em;

    //Markdown에서 form-data를 추출하기 위한 패턴 ![](*)
    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[\\]\\(data:image.*?\\)");


    public void changeRecruitFavorite(Long user_id, Long recruit_id){
        User user = userRepository.findById(user_id).orElseThrow( () -> new NotFoundException("존재하지 않는 유저입니다."));
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        Optional<RecruitFavorite> optFavorite = recruitFavoriteRepository.findById(new RecruitFavoritePK(user_id, recruit_id));
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

    public List<RecruitInterviewDto> getInterviewList(Long recruit_id){
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        List<RecruitInterviewDto> result = new ArrayList<>();
        for (RecruitInterview question: recruit.getInterviews()) {
            RecruitInterviewDto recruitInterviewDto = RecruitInterviewDto.builder()
                    .question(question.getQuestion())
                    .type(question.getType())
                    .optionList(question.getOptions())
                    .build();
            result.add(recruitInterviewDto);
        }

        return result;
    }

    public List<TeamApplicantListDto> getTeamApplicantList(Long user_id){
        //TODO:모듈화 리팩토링 필요
        User user = userRepository.findById(user_id).orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        List<RecruitApplicant> recruitApplicantList = recruitApplicantRepository.findByUserId(user_id);
        List<TeamApplicantListDto> result = new ArrayList<>();

        //questionList 이터레이트 하면서 dtoList만들기
        for (RecruitApplicant recruitApplicant : recruitApplicantList) {
            ArrayList<RecruitAnswerDto> answerDtoList = new ArrayList<>();
            List<String> answerList = recruitApplicant.getAnswerList();
            List<RecruitInterview> questionList = recruitApplicant.getRecruit().getInterviews();
            int index = 0;
            for (RecruitInterview question: questionList) {
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

    public Page<RecruitListResponse> getRecruitSearchList(Pageable pageable, RecruitRequest request, Authentication auth) {

        //TODO:favorite 등
        //query 생성 준비
        String[] dues = {"1주일", "2주일", "3주일", "1달", "2달", "3달"};

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Recruit> cq = cb.createQuery(Recruit.class).distinct(true);
        Root<Recruit> recruit = cq.from(Recruit.class);
        List<Predicate> predicates = new ArrayList<>();

        // query 생성
        predicates.add(cb.equal(recruit.get("status"), RecruitStatus.ONGOING));
        if (request.getTag() != null && !request.getTag().isEmpty()) {
            Join<Recruit, String> tagList = recruit.join("tags");
            predicates.add(tagList.in(request.getTag()));
        }
        if (request.getType() != null && !request.getType().isEmpty()){
            predicates.add(cb.equal(recruit.get("type"), TeamType.from(request.getType())));
        }
        if (request.getPlace() != null && !request.getPlace().isEmpty()) {
            predicates.add(cb.equal(recruit.get("place"), TeamOperationFormat.from(request.getPlace())));
        }
        if (request.getRegion() != null && !request.getRegion().isEmpty()) {
            predicates.add(cb.equal(recruit.get("region"), request.getRegion()));
        }
        if (request.getDue() != null && !request.getDue().isEmpty()) {
            int index = Arrays.asList(dues).indexOf(request.getDue());
            if (index != -1) {
                List<String> validDues = Arrays.asList(dues).subList(0, index + 1);
                predicates.add(recruit.get("due").in(validDues));
            }
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
        cq.where(predicates.toArray(new Predicate[0])).orderBy(orders);
        List<Recruit> recruits = em.createQuery(cq).getResultList();
        System.out.println(recruits.size());

        List<RecruitListResponse> results = recruits.stream()
                .map(recruit2 -> new RecruitListResponse(
                        recruit2.getTitle(),
                        recruit2.getThumbnailUrl(),
                        recruit2.getWriterId(),
                        recruit2.getWriter().getNickname(),
                        recruit2.getWriter().getImageUrl(),
                        recruit2.getStatus().toString(),
                        recruit2.getTags(),
                        ((auth != null) &&
                                (recruitFavoriteRepository
                                        .findById(new RecruitFavoritePK(User.authenticationToUser(auth).getId(), recruit2.getId()))
                                        .isPresent()))))
                .collect(Collectors.toList());

        return  new PageImpl<>(results, pageable, results.size());
    }

    @Transactional
    public RecruitResponce getRecruit(Long recruit_id){
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        recruit.setHit(recruit.getHit() + 1);
        List<RecruitRoleDTO> roleDtoList = new ArrayList<>();
        for (RecruitRole role: recruit.getRoles()) {
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
                .tagList(recruit.getTags())
                .roleList(roleDtoList)
                .build();
    }

    public RecruitUpdateResponse getRecruitwithInterviewList(Long recruit_id){
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        List<RecruitRoleDTO> roleDtoList = new ArrayList<>();
        for (RecruitRole role: recruit.getRoles()) {
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
                .tagList(recruit.getTags())
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

    private Team createTeam(User user, RecruitListRequestDTO recruitListRequestDTO){
        Team team = Team.builder()
                .name(recruitListRequestDTO.getName())
                .type(TeamType.from(recruitListRequestDTO.getType()))
                .dueTo(recruitListRequestDTO.getDue())
                .operationFormat(TeamOperationFormat.from(recruitListRequestDTO.getPlace()))
                .status(TeamStatus.RECRUITING)
                .teamMemberStatus(TeamMemberStatus.RECRUITING)
                .isLock(false)
                .region1(recruitListRequestDTO.getRegion().get(0))
                .region2(recruitListRequestDTO.getRegion().get(1))
                .region3(null)
                .build();
        teamRepository.save(team);// 리더 추가
        TeamUser teamUser = TeamUser.builder()
                .teamId(team.getId())
                .userId(user.getId())
                .role(TeamUserRoleType.LEADER)
                .job("Leader")
                .build();
        teamUserRepository.save(teamUser);
        return team;
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
        if (result.isEmpty()){
            result.add("");
        }
        matcher.appendTail(sb);
        result.add(sb.toString());
        return result;
    }

    private Recruit createRecruitFromDto(RecruitListRequestDTO recruitListRequestDTO, Team team, User user) throws IOException{
        List<String> content = processMarkdownWithFormData(recruitListRequestDTO.getContent());
        Recruit recruit = Recruit.builder()
                .team(team)
                .type(TeamType.from(recruitListRequestDTO.getType()))
                .title(recruitListRequestDTO.getTitle())
                .due(recruitListRequestDTO.getDue())
                .link(recruitListRequestDTO.getLink())
                .content(content.get(1))
                .place(TeamOperationFormat.from(recruitListRequestDTO.getPlace()))
                .region1(recruitListRequestDTO.getPlace().equals("온라인") ? null : recruitListRequestDTO.getRegion().get(0))
                .region2(recruitListRequestDTO.getPlace().equals("온라인") ? null : recruitListRequestDTO.getRegion().get(1))
                .tags(recruitListRequestDTO.getTagList())
                .status(RecruitStatus.ONGOING)
                .thumbnailUrl((content.get(0).isBlank()) ? null : content.get(0))
                .writerId(user.getId())
                .writer(user)
                .hit(0L)
                .build();
        //List 추가
        addInterviewsToRecruit(recruit, recruitListRequestDTO.getInterviewList());
        addRolesToRecruit(recruit, recruitListRequestDTO.getRoleList());
        return recruit;
    }


    @Transactional
    public void createRecruit(RecruitListRequestDTO recruitListRequestDTO, Authentication auth) throws IOException{
        //동일한 팀 이름 검사
        Optional<Team> findTeam = teamRepository.findByName(recruitListRequestDTO.getName());
        if (findTeam.isPresent())
            throw new IllegalArgumentException("이미 존재하는 팀 이름입니다.");
        User user = User.authenticationToUser(auth);
        //팀 생성
        Team team = createTeam(user, recruitListRequestDTO);

        //모집게시글 생성
        Recruit recruit = createRecruitFromDto(recruitListRequestDTO, team, user);
        System.out.println(recruit.getContent());
        recruitRepository.save(recruit);
    }

    @Transactional
    public void applyRecruit(Long recruit_id, ApplyRecruitRequest request){
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        Optional <RecruitApplicant> optRecruitApplicant = recruitApplicantRepository.findById(new RecruitApplicantPK(recruit_id, request.getUser_id()));
        if (optRecruitApplicant.isPresent()){
            throw new ConflictException("이미 지원한 팀입니다.");
        }
        RecruitApplicant recruitApplicant = RecruitApplicant.builder()
                .recruitId(recruit_id)
                .userId(request.getUser_id())
                .nickname(userRepository.findById(request.getUser_id()).get().getNickname())
                .status(RecruitApplicantStatus.PENDING)
                .answerList(request.getAnswerList())
                .build();
        recruitApplicantRepository.save(recruitApplicant);
    }

    @Transactional
    public void deleteRecruit(Long recruit_id){
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(
                () -> new NotFoundException("존재하지 않는 모집게시글입니다."));
        recruitRepository.delete(recruit);
    }

    @Transactional
    public void updateRecruit(Long recruit_id, RecruitUpdateRequestDTO recruitUpdateRequestDTO) throws IOException{
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(
                () -> new NotFoundException("존재하지 않는 모집게시글입니다."));

        List<String> content = processMarkdownWithFormData(recruitUpdateRequestDTO.getContent());
        recruit.update(recruitUpdateRequestDTO, content);
    }

    public List<TagListResponse> getTagList(){
        List<TagListResponse> result = new ArrayList<>();
        result.add(new TagListResponse("Java", "#9AFE2E"));
        result.add(new TagListResponse("JavaScript", "#045FB4"));
        result.add(new TagListResponse("React", "#FF8000"));
        result.add(new TagListResponse("SpringBoot", "#FE2EC8"));
        return result;
    }
}
