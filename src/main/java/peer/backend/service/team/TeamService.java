package peer.backend.service.team;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.CompleteTeamTracking;
import peer.backend.annotation.tracking.DisperseTeamTracking;
import peer.backend.annotation.tracking.TeamCreateTracking;
import peer.backend.dto.board.recruit.RecruitAnswerDto;
import peer.backend.dto.board.recruit.RecruitCreateRequest;
import peer.backend.dto.noti.enums.NotificationPriority;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.dto.team.TeamApplicantListDto;
import peer.backend.dto.team.TeamInfoResponse;
import peer.backend.dto.team.TeamJobCreateRequest;
import peer.backend.dto.team.TeamJobDto;
import peer.backend.dto.team.TeamJobUpdateDto;
import peer.backend.dto.team.TeamListResponse;
import peer.backend.dto.team.TeamMemberDto;
import peer.backend.dto.team.TeamSettingDto;
import peer.backend.dto.team.TeamSettingInfoDto;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.enums.RecruitDueEnum;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.composite.TeamUserJobPK;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamJob;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.TeamUserJob;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.team.enums.TeamUserStatus;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.ConflictException;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.IllegalArgumentException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.team.BoardRepository;
import peer.backend.repository.team.TeamJobRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserJobRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.service.TeamUserService;
import peer.backend.service.file.ObjectService;
import peer.backend.service.noti.NotificationCreationService;
import peer.backend.service.profile.UserPortfolioService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final ObjectService objectService;
    private final TeamJobRepository teamJobRepository;
    private final TeamUserJobRepository teamUserJobRepository;
    private final TeamUserService teamUserService;
    private final BoardRepository boardRepository;
    private final EntityManager em;
    private final UserPortfolioService userPortfolioService;

    private final NotificationCreationService notificationCreationService;

    private final static String teamPage = "/teams/";
    private final static String teamList = "/team-list";

    public boolean isLeader(Long teamId, User user) {
        return teamUserRepository.findTeamUserRoleTypeByTeamIdAndUserId(teamId, user.getId())
            == TeamUserRoleType.LEADER;
    }

    private boolean checkValidationForApprovedOrNot(User user, Team team) {
        AtomicBoolean result = new AtomicBoolean(false);

        team.getTeamUsers().forEach(member -> {
            if (member.getId().equals(user.getId())) {
                result.set(member.getStatus().equals(TeamUserStatus.APPROVED));
            }
        });
        return result.get();
    }


    @Transactional
    public List<TeamListResponse> getTeamList(TeamStatus teamStatus, User user) {
        if (teamStatus == null) {
            throw new NotFoundException("존재하지 않는 팀 상태 입니다.");
        }
        List<TeamUser> teamUserList = teamUserRepository.findByUserId(user.getId());
        List<TeamListResponse> teamListResponse = new ArrayList<>();
        for (TeamUser teamUser : teamUserList) {
            Team team = teamUser.getTeam();
            if (teamUser.getTeam().getStatus() == teamStatus) {
                teamListResponse.add(
                    new TeamListResponse(team, teamUser, team.getTeamUsers().size()));
            }
        }
        return teamListResponse;
    }

    @Transactional
    public TeamSettingDto getTeamSetting(Long teamId, User user) {
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        return new TeamSettingDto(team, teamUserRepository.findByTeamId(teamId));
    }

    @Transactional
    public void updateTeamSetting(Long teamId, TeamSettingInfoDto teamSettingInfoDto, User user) {
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        String teamImage = teamSettingInfoDto.getTeamImage();
        if (teamImage != null) {
            if (teamImage.startsWith("data:image/png;base64")) {
                teamImage = teamImage.replace("data:image/png;base64,", "");
            } else if (teamImage.startsWith("data:image/jpg;base64")) {
                teamImage = teamImage.replace("data:image/jpg;base64,", "");
            } else if (teamImage.startsWith("data:image/jpeg;base64")) {
                teamImage = teamImage.replace("data:image/jpeg;base64,", "");
            }
            teamSettingInfoDto.setTeamImage(teamImage);
        }
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));

        if (!this.validateUpdatable(team.getStatus())) {
            throw new ConflictException("팀이 해산이나 완료 상태일 경우 정보를 수정할 수 없습니다!");
        }

        if (!this.validateRequestTeamStatusEnum(team, teamSettingInfoDto)) {
            throw new BadRequestException("허용되지 않은 Enum값 입니다!");
        }

        if (teamId.equals(Long.parseLong(teamSettingInfoDto.getId())) && isLeader(teamId, user)) {
            String filePath = "TeamImage";
            if (team.getTeamLogoPath() != null) {
                if (teamSettingInfoDto.getTeamImage() != null) {
                    if (!team.getTeamLogoPath().equals(teamSettingInfoDto.getTeamImage())) {
                        String newImage = objectService.uploadObject(
                                filePath + "/" + team.getId().toString(),
                                teamSettingInfoDto.getTeamImage(), "image");
                        objectService.deleteObject(team.getTeamLogoPath());
                        team.setTeamLogoPath(newImage);
                        this.userPortfolioService.setTeamLogoPath(team.getId(), newImage);
                    }
                } else {
                    objectService.deleteObject(team.getTeamLogoPath());
                    team.setTeamLogoPath(null);
                }
            } else if (teamSettingInfoDto.getTeamImage() != null) {
                String newImage = objectService.uploadObject(
                    filePath + "/" + team.getId().toString(),
                    teamSettingInfoDto.getTeamImage(), "image");
                team.setTeamLogoPath(newImage);
            }
            team.update(teamSettingInfoDto);
        } else {
            throw new ForbiddenException("팀장이 아니거나 팀 아이디가 일치하지 않습니다.");
        }

        this.notificationCreationService.makeNotificationForTeam(
                null,
                "팀 설정이 변경되었습니다. 확인 부탁드립니다.",
                teamPage + teamId,
                NotificationPriority.IMMEDIATE,
                NotificationType.TEAM,
                null,
                team.getId(),
                team.getTeamLogoPath()
        );
    }

    @Transactional
    public ArrayList<TeamMemberDto> deleteTeamMember(Long teamId, Long deletingToUserId,
        User user) {
        if (deletingToUserId.equals(user.getId())) {
            throw new ForbiddenException("자기 자신을 팀에서 추방할 수 없습니다.");
        }
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        boolean isRemoved = team.deleteTeamUser(deletingToUserId);
        if (!isRemoved) {
            throw new ForbiddenException("삭제할 유저는 팀장이 아닙니다!");
        }

        this.notificationCreationService.makeNotificationForUser(
                null,
                "당신은 " + team.getName() + " 팀에서 추방당하셨습니다. 비정상적인 처리일 경우 peer 운영팀에에 문의 부탁드립니다.",
                "",
                NotificationPriority.IMMEDIATE,
                NotificationType.TEAM,
                null,
                deletingToUserId,
                null);

        return team.getTeamUsers().stream().map(TeamMemberDto::new)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Transactional
    public void grantRole(Long teamId, Long grantingUserId, User user,
        TeamUserRoleType teamUserRoleType) {
        if (grantingUserId.equals(user.getId())) {
            throw new ForbiddenException("자기 자신에게 권한을 부여할 수 없습니다.");
        }
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        team.grantLeaderPermission(grantingUserId, teamUserRoleType);

        String role;
        if (teamUserRoleType.equals(TeamUserRoleType.LEADER)) {
            role = "팀 리더";
        } else
            role = "팀 멤버";

        this.notificationCreationService.makeNotificationForUser(
                null,
                "당신은 " + team.getName() + " 팀의 " + role + " 로 설정되셨습니다.",
                teamPage + team.getId(),
                NotificationPriority.IMMEDIATE,
                NotificationType.TEAM,
                null,
                grantingUserId,
                team.getTeamLogoPath()
        );
    }

    @Transactional
    public void exitTeam(Long teamId, User user) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        TeamUser teamUser = this.teamUserService.getTeamUser(user.getId(), teamId);
        if (!team.deleteTeamUser(teamUser.getUserId())) {
            throw new NotFoundException("탈퇴할 수 없습니다.");
        } else {
            if (team.getTeamUsers().isEmpty()) {
                teamRepository.delete(team);
            } else {
                team.grantLeaderPermission(team.getTeamUsers().get(0).getUserId(),
                    TeamUserRoleType.LEADER);
            }
        }

        this.notificationCreationService.makeNotificationForTeam(
                null,
                "아쉽게도 " + user.getNickname() + " 님께서 팀에서 나가셨습니다.",
                teamPage + teamId,
                NotificationPriority.IMMEDIATE,
                NotificationType.TEAM,
                null,
                teamId,
                team.getTeamLogoPath()
        );
    }

    @Transactional
    public List<TeamApplicantListDto> getTeamApplicantList(Long teamId, User user) {
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (team.getStatus() != TeamStatus.RECRUITING) {
            throw new NotFoundException("모집이 진행중이 아닙니다.");
        }

        List<TeamUserJob> applicants = teamUserJobRepository.findByTeamJobTeamIdAndStatus(team,
            TeamUserStatus.PENDING);

        List<TeamApplicantListDto> result = new ArrayList<>();
        for (TeamUserJob applicant : applicants) {
            List<RecruitAnswerDto> answerDtoList = new ArrayList<>();
            List<String> answerList = applicant.getAnswers();
            List<RecruitInterview> questionList = team.getRecruit().getInterviews();
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
            User applicantUser = applicant.getTeamUser().getUser();
            result.add(TeamApplicantListDto.builder()
                .answers(answerDtoList)
                .name(applicantUser.getNickname())
                .jobName(applicant.getTeamJob().getName())
                .userId(applicantUser.getId())
                .applyId(new TeamUserJobPK(applicant.getTeamUserId(), applicant.getTeamJobId()))
                .image(applicantUser.getImageUrl())
                .build());
        }

        // 신청자를 위한 알림
        this.notificationCreationService.makeNotificationForUser(
                null,
                "축하드립니다! " + team.getName() + " 팀에 신청을 완료하였습니다. 답변이 올 때까지 기다려볼까요? 궁금한 것은 팀장에게 메시지를 날려보아도 좋습니다!",
                teamList,
                NotificationPriority.IMMEDIATE,
                NotificationType.SYSTEM,
                null,
                user.getId(),
                null
        );

        //팀리더에게 알림
        List<TeamUser> owner = team.getTeamUsers().stream().filter(m -> m.getRole().equals(TeamUserRoleType.LEADER)).collect(Collectors.toList());
        List<Long> userIds = new ArrayList<>();
        owner.forEach(m -> userIds.add(m.getUserId()));
        this.notificationCreationService.makeNotificationForUserList(
                null,
                team.getName() + " 팀에 새로운 동료 신청이 들어왔습니다! 어떤 분인지 맞이하러 가볼까요?",
                teamPage+ team.getId() + "/setting",
                NotificationPriority.IMMEDIATE,
                NotificationType.TEAM,
                null,
                userIds,
                team.getTeamLogoPath()
        );

        return result;
    }

    @Transactional
    public void acceptTeamApplicant(Long teamId, TeamUserJobPK teamUserJobId, User user) {
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        TeamUserJob teamUserJob = teamUserJobRepository.findById(teamUserJobId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 지원자입니다."));
        teamUserJob.acceptApplicant();

        Team team = this.teamRepository.findById(teamId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 팀입니다.")
        );
        // 신청자에게 알리기
        this.notificationCreationService.makeNotificationForUser(
                null,
                "축하드립니다! 신청하신 팀에서 신청을 수락하였습니다. 팀페이지에서 자세한 내용을 확인해주세요.",
                teamList,
                NotificationPriority.IMMEDIATE,
                NotificationType.SYSTEM,
                null,
                user.getId(),
                null
        );
        this.notificationCreationService.makeNotificationForTeam(
                null,
                "여러분 새로운 동료가 찾아왔습니다. 모두 축하해주세요!",
                teamPage + teamId,
                NotificationPriority.IMMEDIATE,
                NotificationType.TEAM,
                null,
                teamId,
                team.getTeamLogoPath()
        );
    }

    @Transactional
    public void rejectTeamApplicant(Long teamId, TeamUserJobPK applicantId, User user) {
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        TeamUserJob teamUserJob = teamUserJobRepository.findById(applicantId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 지원자입니다."));
        teamUserJobRepository.delete(teamUserJob);
        em.flush();
        TeamUser teamUser = teamUserJob.getTeamUser();
        if (teamUser.getTeamUserJobs().isEmpty()) {
            teamUserRepository.delete(teamUser);
        }


        // 신청자에게 알림 보냄
        this.notificationCreationService.makeNotificationForUser(
                null,
                "안타깝게도 지원이 거절 당했습니다. 팀 페이지에서 자세한 내용을 확인해주세요.",
                teamList,
                NotificationPriority.IMMEDIATE,
                NotificationType.SYSTEM,
                null,
                user.getId(),
                null
        );
    }

    @Transactional
    public TeamInfoResponse getTeamInfo(Long teamId, User user) {
//        Team team = this.teamRepository.findById(teamId)
//            .orElseThrow(() -> new NotFoundException("팀이 없습니다"));
//        if (teamUserRepository.existsByUserIdAndTeamId(user.getId(), teamId)) {
//            if (checkValidationForApprovedOrNot(user, team))
//                return new TeamInfoResponse(team);
//            else
//                throw new UnauthorizedException("팀 멤버로 승인되어 있지 않습니다.");
//        } else {
//            throw new ForbiddenException("팀에 속해있지 않습니다.");
//        }
        Team team = this.teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("팀이 없습니다"));
        if (teamUserRepository.existsAndMemberByUserIdAndTeamId(user.getId(), teamId)) {
            return new TeamInfoResponse(team);
        } else {
            throw new ForbiddenException("팀에 속해있지 않습니다.");
        }
    }

    @Transactional
    public List<TeamMemberDto> getTeamMemberList(Long teamId, User user) {
        if (!teamRepository.existsById(teamId)) {
            throw new NotFoundException("팀이 없습니다");
        }
        if (teamUserRepository.existsByUserIdAndTeamIdAndStatus(user.getId(), teamId,
            TeamUserStatus.APPROVED)) {
            return teamUserRepository.findByTeamIdAndStatus(teamId, TeamUserStatus.APPROVED)
                .stream()
                .map(TeamMemberDto::new)
                .collect(Collectors.toList());
        } else {
            throw new ForbiddenException("팀에 속해있지 않습니다.");
        }
    }

    private void addRolesToTeam(Team team, List<TeamJobDto> roleList) {
        if (roleList != null && !roleList.isEmpty()) {
            for (TeamJobDto role : roleList) {
                team.addRole(role);
            }
        }
    }


    @TeamCreateTracking
    @Transactional
    public Team createTeam(User user, RecruitCreateRequest request)
        throws IllegalArgumentException {
        Team team = Team.builder()
            .name(request.getName())
            .type(TeamType.valueOf(request.getType()))
            .operationFormat(TeamOperationFormat.valueOf(request.getPlace()))
            .status(TeamStatus.RECRUITING)
            .teamMemberStatus(TeamMemberStatus.RECRUITING)
            .isLock(false)
            .region1(request.getRegion1())
            .region2(request.getRegion2())
            .dueTo(RecruitDueEnum.from(request.getDue()))
            .maxMember(request.getMax())
            .build();
        if (request.getRoleList() != null) {
            addRolesToTeam(team, request.getRoleList());
        }
        teamRepository.save(team);
        // 리더 추가
        TeamUser teamUser = TeamUser.builder()
            .teamId(team.getId())
            .userId(user.getId())
            .role(TeamUserRoleType.LEADER)
            .status(TeamUserStatus.APPROVED)
            .build();
        teamUserRepository.save(teamUser);
        TeamJob leader = TeamJob.builder()
            .team(team)
            .name("Leader")
            .max(1)
            .build();
        teamJobRepository.save(leader);
        TeamUserJob userLeader = TeamUserJob.builder()
            .teamJobId(leader.getId())
            .teamUserId(teamUser.getId())
            .status(TeamUserStatus.APPROVED)
            .build();
        teamUserJobRepository.save(userLeader);

        Board board = Board.builder()
            .name("공지사항")
            .type(BoardType.NOTICE)
            .team(team)
            .build();
        boardRepository.save(board);

        if (team.getType().equals(TeamType.STUDY)) {
            TeamJob study = TeamJob.builder()
                .team(team)
                .name(TeamType.STUDY.getValue())
                .max(request.getMax())
                .build();
            teamJobRepository.save(study);
        }
        return team;
    }

    @Transactional
    public Page<Team> getTeamListFromPageable(Pageable pageable) {
        return this.teamRepository.findAll(pageable);
    }

    @Transactional
    public Page<Team> getTeamListByNameOrLeaderFromPageable(Pageable pageable, String keyword) {
        return this.teamRepository.findByNameAndLeaderContainingFromPageable(pageable, keyword);
    }

    @Transactional
    public ResponseEntity<Object> updateTeamJob(TeamJobUpdateDto request, User user) {
        TeamJob teamJob = teamJobRepository.findById(request.getJob().getId())
            .orElseThrow(() -> new NotFoundException("존재하지 않는 역할입니다."));

        String oldName = teamJob.getName();

        Team team = teamJob.getTeam();
        if (team.getType().equals(TeamType.STUDY)) {
            throw new BadRequestException("스터디는 역할을 수정할 수 없습니다.");
        }
        if (!isLeader(team.getId(), user)) {
            throw new ForbiddenException("팀의 리더만 역할을 수정할 수 있습니다.");
        }
        if (teamJobRepository.existsByTeamIdAndName(team.getId(), request.getJob().getName())) {
            throw new ConflictException("이미 존재하는 역할 이름 입니다.");
        }
        if (teamJob.getCurrent() > request.getJob().getMax()) {
            throw new ConflictException("최대 인원 수가 현재 배정된 인원 수 보다 작습니다!");
        }
        teamJob.update(request.getJob());

        // 팀 설정 변경에 대한 알림 전달
        String newName = request.getJob().getName();
        String body;
        if (oldName.equals(newName))
            body = "팀의 " + oldName + " 역할이 수정이 되었습니다.";
        else
            body = "팀의 " + oldName + " 은 " + newName + "으로 변경되었습니다.";
        this.notificationCreationService.makeNotificationForTeam(
                null,
                body,
                teamPage + team.getId(),
                NotificationPriority.IMMEDIATE,
                NotificationType.TEAM,
                null,
                team.getId(),
                team.getTeamLogoPath()
        );

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> createTeamJob(Long teamId, TeamJobCreateRequest request,
        User user) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (team.getType().equals(TeamType.STUDY)) {
            throw new BadRequestException("스터디에는 역할을 추가할 수 없습니다.");
        }
        if (teamJobRepository.existsByTeamIdAndName(teamId, request.getJob().getName())) {
            throw new ConflictException("이미 존재하는 역할 이름 입니다.");
        }
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀의 리더만 역할을 추가할 수 있습니다.");
        }
        team.addRole(request.getJob());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> deleteTeamJob(Long jobId, User user) {
        TeamJob teamJob = teamJobRepository.findById(jobId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 역할입니다."));
        if (teamJob.getTeam().getType().equals(TeamType.STUDY)) {
            throw new BadRequestException("스터디는 역할을 수정할 수 없니다.");
        }
        if (Objects.nonNull(teamJob.getTeamUserJobs()) && !teamJob.getTeamUserJobs().isEmpty()) {
            throw new ConflictException("역할에 이미 배정된 인원이 있습니다.");
        }
        if (!isLeader(teamJob.getTeam().getId(), user)) {
            throw new ForbiddenException("리더가 아닙니다.");
        }
        teamJobRepository.delete(teamJob);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Transactional
    public void quitTeam(User user, Long teamId) {
        // 알림에 필요한 요소를 위해 일부 수정
        Team team = this.getTeamByTeamId(teamId);
        if (this.isLeader(teamId, user)) {
            SecureRandom rm = new SecureRandom();
            List<TeamUser> teamUserList = team.getTeamUsers();
            if (teamUserList.size() == 1) {
                throw new ConflictException("팀 인원이 1명일 경우 팀을 나갈 수 없습니다. 해산하기나 완료하기를 해주십시오.");
            }
            teamUserList.forEach(teamUser -> {
                if (teamUser.getUserId().equals(user.getId())) {
                    teamUserList.remove(teamUser);
                }
            });
            int randomIndex = rm.nextInt(teamUserList.size());
            TeamUser teamUser = teamUserList.get(randomIndex);
            teamUser.setRole(TeamUserRoleType.LEADER);
        } else {
            this.teamUserService.deleteTeamUser(user.getId(), teamId);
        }

        // 팀 나가기 되었음을 알림
        this.notificationCreationService.makeNotificationForTeam(
            null,
                user.getNickname() + " 님께서 팀을 나가셨습니다.",
                teamPage + teamId,
                NotificationPriority.IMMEDIATE,
                NotificationType.TEAM,
                null,
                teamId,
                team.getTeamLogoPath()
        );
    }

    @Transactional
    public Team getTeamByTeamId(Long teamId) {
        return this.teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀 Id 입니다."));
    }

    @DisperseTeamTracking
    @Transactional
    public Team disperseTeam(User user, Long teamId) {
        if (!this.isLeader(teamId, user)) {
            throw new ForbiddenException("팀의 리더만 팀을 해산 할 수 있습니다!");
        }
        Team team = this.getTeamByTeamId(teamId);
        if (team.getStatus().equals(TeamStatus.RECRUITING)) {
            throw new ConflictException("팀이 모집 중 상태일 경우 팀을 해산 할 수 없습니다!");
        }
        team.setStatus(TeamStatus.DISPERSE);
        team.getRecruit().setStatus(RecruitStatus.DONE);

        this.notificationCreationService.makeNotificationForTeam(
                null,
                team.getName() + " 팀이 해산되었슴을 알립니다.",
                teamList,
                NotificationPriority.IMMEDIATE,
                NotificationType.SYSTEM,
                null,
                team.getId(),
                null
        );

        return team;
    }

    @CompleteTeamTracking
    @Transactional
    public Team completeTeam(User user, Long teamId) {
        if (!this.isLeader(teamId, user)) {
            throw new ForbiddenException("팀의 리더만 팀을 완료 할 수 있습니다!");
        }
        Team team = this.getTeamByTeamId(teamId);
        if (team.getStatus().equals(TeamStatus.RECRUITING)) {
            throw new ConflictException("팀이 모집 중 상태일 경우 팀을 완료 할 수 없습니다!");
        }
        team.setStatus(TeamStatus.COMPLETE);
        team.setEnd(LocalDateTime.now());

        // 팀 완료되었음을 알림
        this.notificationCreationService.makeNotificationForTeam(
                null,
                team.getName() + " 팀이 성공적으로 마무리 되었습니다! 지금까지의 이야기를 쇼케이스, 피어로그로 남겨보세요. 여러분의 이야기가 누군가의 길잡이가 되어줄 것입니다.",
                teamPage + teamId,
                NotificationPriority.FORCE,
                NotificationType.TEAM,
                null,
                team.getId(),
                team.getTeamLogoPath()
        );
        return team;
    }

    private boolean validateRequestTeamStatusEnum(Team team,
        TeamSettingInfoDto teamSettingInfoDto) {
        if (!team.getStatus().equals(teamSettingInfoDto.getStatus())) {
            TeamStatus requestStatus = teamSettingInfoDto.getStatus();
            return requestStatus.equals(TeamStatus.RECRUITING) || requestStatus.equals(
                TeamStatus.BEFORE) || requestStatus.equals(TeamStatus.ONGOING);
        }
        return true;
    }

    private boolean validateUpdatable(TeamStatus status) {
        return !status.equals(TeamStatus.DISPERSE) && !status.equals(TeamStatus.COMPLETE);
    }
}
