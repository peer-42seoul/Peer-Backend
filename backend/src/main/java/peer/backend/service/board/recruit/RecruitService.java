package peer.backend.service.board.recruit;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import peer.backend.dto.Board.Recruit.RecruitUpdateRequestDTO;
import peer.backend.dto.board.recruit.*;
import peer.backend.dto.team.TeamApplicantListDto;
import peer.backend.entity.board.recruit.*;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.composite.RecruitFavoritePK;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.*;
import peer.backend.entity.user.User;
import peer.backend.exception.IllegalArgumentException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.recruit.RecruitApplicantRepository;
import peer.backend.repository.board.recruit.RecruitFavoriteRepository;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
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

    //query 생성 및 주입
    @PersistenceContext
    private EntityManager em;

    //Markdown에서 form-data를 추출하기 위한 패턴 ![](*)
    private static final Pattern IMAGE_PATTERN = Pattern.compile("!\\[\\]\\(data:image.*?\\)");


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
                    .nickName(user.getNickname())
                    .recruitId(recruitApplicant.getRecruitId())
                    .build());
        }
        return result;
    }

    public Page<RecruitListResponse> getRecruitSearchList(Pageable pageable, RecruitRequest request, Long user_id) {

        //TODO:favorite 등록
        //query 생성 준비
        String[] dues = {"1주일", "2주일", "3주일", "1달", "2달", "3달"};

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Recruit> cq = cb.createQuery(Recruit.class);
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
        switch(request.getSort()) {
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

        // recruitResponseDto 매핑
        List<RecruitListResponse> results = recruits.stream()
                .map(recruit2 -> new RecruitListResponse(
                        recruit2.getTitle(),
                        recruit2.getThumbnailUrl(),
                        recruit2.getWriterId(),
                        recruit2.getWriter().getNickname(),
                        recruit2.getWriter().getImageUrl(),
                        recruit2.getStatus().toString(),
                        recruit2.getTags(),
                        true
                )).collect(Collectors.toList());


        for (Recruit recruit1 : recruits) {
            System.out.println(recruit1.getTitle() + " " + recruit1.getCreatedAt() + " " + recruit1.getHit());
        }
        return  new PageImpl<>(results, pageable, results.size());
    }

    public Page<RecruitListResponse> getRecruitList(int page, int pageSize, Principal principal){
        List<Recruit> recruits = recruitRepository.findAll();
        Pageable pageable = PageRequest.of(page, pageSize);
        User user = userRepository.findByName(principal.getName()).orElseThrow(
                () -> new NotFoundException("존재하지 않는 유저입니다."));
        //TODO:userImage를 thumbnail로 변환하여 매핑
        List<RecruitListResponse> result = new ArrayList<>();
        for (Recruit recruit: recruits) {
            User writer = userRepository.findById(recruit.getWriterId()).orElseThrow(
                    () -> new NotFoundException("존재하지 않는 유저입니다."));
            RecruitListResponse recruitListResponse = RecruitListResponse.builder()
                    .title(recruit.getTitle())
                    .tagList(recruit.getTags())
                    .status(recruit.getStatus().toString())
                    .image(recruit.getThumbnailUrl())
                    .user_thumbnail(writer.getImageUrl())
                    .user_nickname(writer.getNickname())
                    .user_id(recruit.getWriterId())
                    .build();
            RecruitFavoritePK recruitFavoritePK = new RecruitFavoritePK(user.getId(), recruit.getId());
            Optional<RecruitFavorite> recruitFavorite = recruitFavoriteRepository.findById(recruitFavoritePK);
            if (recruitFavorite.isPresent())
                recruitListResponse.setFavorite(true);
            result.add(recruitListResponse);
        }
        return new PageImpl<>(result, pageable, result.size());
    }

    public RecruitResponce getRecruit(Long recruit_id){
        Recruit recruit = recruitRepository.findById(recruit_id).orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        //TODO:DTO 항목 추가 필요
        return RecruitResponce.builder()
                .name(recruit.getTitle())
                .build();
    }


    private Team createTeam(User user, RecruitRequestDTO recruitRequestDTO){
        Team team = Team.builder()
                .name(recruitRequestDTO.getName())
                .type(TeamType.from(recruitRequestDTO.getType()))
                .dueTo(recruitRequestDTO.getDue())
                .operationFormat(TeamOperationFormat.from(recruitRequestDTO.getPlace()))
                .status(TeamStatus.RECRUITING)
                .teamMemberStatus(TeamMemberStatus.RECRUITING)
                .isLock(false)
                .region1(recruitRequestDTO.getRegion())
                .region2(recruitRequestDTO.getRegion())
                .region3(recruitRequestDTO.getRegion())
                .build();
        teamRepository.save(team);
        // 리더 추가
        TeamUser teamUser = TeamUser.builder()
                .team(team)
                .user(user)
                .role(TeamUserRoleType.LEADER)
                .build();
        return team;
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

    private Recruit createRecruitFromDto(RecruitRequestDTO recruitRequestDTO, Team team) throws IOException{
        Recruit recruit = Recruit.builder()
                .team(team)
                .type(TeamType.from(recruitRequestDTO.getType()))
                .title(recruitRequestDTO.getTitle())
                .due(recruitRequestDTO.getDue())
                .link(recruitRequestDTO.getLink())
                .content(processMarkdownWithFormData(recruitRequestDTO.getContent()))
                .place(TeamOperationFormat.from(recruitRequestDTO.getPlace()))
                .region(recruitRequestDTO.getRegion())
                .tags(recruitRequestDTO.getTagList())
                .status(RecruitStatus.ONGOING)
                .writerId(recruitRequestDTO.getUserId())
                .writer(userRepository.findById(recruitRequestDTO.getUserId()).orElseThrow( () -> new NotFoundException("존재하지 않는 유저입니다.")))
                .build();
        //List 추가
        addInterviewsToRecruit(recruit, recruitRequestDTO.getInterviewList());
        addRolesToRecruit(recruit, recruitRequestDTO.getRoleList());
        return recruit;
    }


    private String processMarkdownWithFormData(String markdown) throws IOException {
        //TODO:Storage에 맞춰 filePath 수정, fileType검사, file 모듈로 리팩토링, fileList에 추가
        Matcher matcher = IMAGE_PATTERN.matcher(markdown);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String formData = matcher.group().substring(26, matcher.group().length() - 1);
            byte[] imageBytes = Base64.getDecoder().decode(formData);
            UUID uuid = UUID.randomUUID();
            Path path = Paths.get("/Users/jwee/upload", UUID.randomUUID().toString() + ".png");
            Files.write(path, imageBytes);
            matcher.appendReplacement(sb, "![image](" + path.toString() + ")");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Transactional
    public void createRecruit(RecruitRequestDTO recruitRequestDTO) throws IOException{
        //TODO:첫번째이미지 대표 이미지 등록 필요
        //유저 검사
        User user = userRepository.findById(recruitRequestDTO.getUserId()).orElseThrow(
                () -> new NotFoundException("사용자를 찾을 수 없습니다.")
        );
        //동일한 팀 이름 검사
        Optional<Team> findTeam = teamRepository.findByName(recruitRequestDTO.getName());
        if (findTeam.isPresent())
            throw new IllegalArgumentException("이미 존재하는 팀 이름입니다.");

        //팀 생성
        Team team = createTeam(user, recruitRequestDTO);

        //모집게시글 생성
        Recruit recruit = createRecruitFromDto(recruitRequestDTO, team);
        System.out.println(recruit.getContent());
        recruitRepository.save(recruit);
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

        String content = processMarkdownWithFormData(recruitUpdateRequestDTO.getContent());
        recruit.update(recruitUpdateRequestDTO, content);
    }
}
