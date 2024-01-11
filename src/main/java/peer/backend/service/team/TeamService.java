package peer.backend.service.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.TeamCreateTracking;
import peer.backend.dto.board.recruit.RecruitAnswerDto;
import peer.backend.dto.board.recruit.RecruitCreateRequest;
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
import peer.backend.repository.team.TeamJobRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserJobRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.service.file.ObjectService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final ObjectService objectService;
    private final TeamJobRepository teamJobRepository;
    private final TeamUserJobRepository teamUserJobRepository;
    private final EntityManager em;

    public boolean isLeader(Long teamId, User user) {
        return teamUserRepository.findTeamUserRoleTypeByTeamIdAndUserId(teamId, user.getId())
            == TeamUserRoleType.LEADER;
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
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (teamId.equals(Long.parseLong(teamSettingInfoDto.getId())) && isLeader(teamId, user)) {
            String filePath = "TeamImage";
            if (team.getTeamLogoPath() != null) {
                if (teamSettingInfoDto.getTeamImage() != null) {
                    if (team.getTeamLogoPath().equals(teamSettingInfoDto.getTeamImage())) {
                        return;
                    }
                    String newImage = objectService.uploadObject(
                        filePath + "/" + team.getId().toString(),
                        teamSettingInfoDto.getTeamImage(), "image");
                    objectService.deleteObject(team.getTeamLogoPath());
                    team.setTeamLogoPath(newImage);
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
    }

    @Transactional
    public void exitTeam(Long teamId, User user) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        TeamUser teamUser = teamUserRepository.findByUserIdAndTeamId(user.getId(), teamId);
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
        //TODO: 신청자에게 알림을 보내야됨
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
        //TODO: 신청자에게 알림을 보내야됨
    }

    @Transactional
    public TeamInfoResponse getTeamInfo(Long teamId, User user) {
        Team team = this.teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("팀이 없습니다"));
        if (teamUserRepository.existsByUserIdAndTeamId(user.getId(), teamId)) {
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
    public ResponseEntity<Object> updateTeamJob(TeamJobUpdateDto request, Authentication auth) {
        User user = User.authenticationToUser(auth);

        request.getJob().forEach(j -> {
                TeamJob teamJob = teamJobRepository.findById(j.getId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 역할입니다."));
                Team team = teamJob.getTeam();
            if (team.getType().equals(TeamType.STUDY)) {
                throw new BadRequestException("스터디는 역할을 수정할 수 없습니다.");
            }
            if (!isLeader(team.getId(), user)) {
                throw new ForbiddenException("리더가 아닙니다.");
            }
                teamJob.update(j);
            }
        );
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> createTeamJob(Long teamId, TeamJobCreateRequest request,
        Authentication auth) {
        User user = User.authenticationToUser(auth);
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (team.getType().equals(TeamType.STUDY)) {
            throw new BadRequestException("스터디에는 역할을 추가할 수 없습니다.");
        }
        if (teamJobRepository.existsByTeamIdAndName(teamId, request.getJob().getName())) {
            throw new ConflictException("이미 있는 역할입니다.");
        }
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("리더가 아닙니다.");
        }
        team.addRole(request.getJob());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> deleteTeamJob(Long jobId, Authentication auth) {
        User user = User.authenticationToUser(auth);
        TeamJob teamJob = teamJobRepository.findById(jobId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 역할입니다."));
        if (teamJob.getTeam().getType().equals(TeamType.STUDY)) {
            throw new BadRequestException("스터디는 역할을 수정할 수 없니다.");
        }
        if (!Objects.isNull(teamJob.getTeamUserJobs()) && !teamJob.getTeamUserJobs().isEmpty()) {
            throw new ConflictException("역할에 이미 배정된 인원이 있습니다.");
        }
        if (!isLeader(teamJob.getTeam().getId(), user)) {
            throw new ForbiddenException("리더가 아닙니다.");
        }
        teamJobRepository.delete(teamJob);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
