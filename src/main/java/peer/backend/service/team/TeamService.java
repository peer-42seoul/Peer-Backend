package peer.backend.service.team;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.TeamCreateTracking;
import peer.backend.dto.board.recruit.RecruitAnswerDto;
import peer.backend.dto.board.recruit.RecruitCreateRequest;
import peer.backend.dto.team.*;
import peer.backend.entity.board.recruit.Recruit;
//import peer.backend.entity.board.recruit.RecruitApplicant;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.*;
import peer.backend.entity.user.User;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
//import peer.backend.repository.board.recruit.RecruitApplicantRepository;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.team.TeamJobRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.service.file.ObjectService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final RecruitRepository recruitRepository;
//    private final RecruitApplicantRepository recruitApplicantRepository;
    private final ObjectService objectService;
    private final TeamJobRepository teamJobRepository;

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
        List<TeamUser> teamUserList = teamUserRepository.findByTeamId(teamId);
        List<TeamApplicantListDto> result = new ArrayList<>();
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (team.getStatus() != TeamStatus.RECRUITING) {
            throw new NotFoundException("모집이 진행중이 아닙니다.");
        }
        //questionList 이터레이트 하면서 dtoList만들기
        for (TeamUser teamUser : teamUserList) {
            ArrayList<RecruitAnswerDto> answerDtoList = new ArrayList<>();
            List<String> answerList = teamUser.getAnswers();
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
            result.add(TeamApplicantListDto.builder()
                .answers(answerDtoList)
                .name(teamUser.getUser().getNickname())
                .userId(teamUser.getUserId())
                .build());
        }
        return result;
    }

    @Transactional
    public void acceptTeamApplicant(Long teamId, Long applicantId, User user) {
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        TeamUser teamUser = teamUserRepository.findByUserIdAndTeamId(applicantId, teamId);
        if (teamUser == null) {
            throw new NotFoundException("존재하지 않는 지원자입니다.");
        }
        teamUser.acceptApplicant();
        //TODO: 신청자에게 알림을 보내야됨
    }

    @Transactional
    public void rejectTeamApplicant(Long teamId, Long applicantId, User user) {
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        TeamUser recruitApplicant = teamUserRepository.findByUserIdAndTeamId(applicantId, teamId);
        if (recruitApplicant == null) {
            throw new NotFoundException("존재하지 않는 지원자입니다.");
        }
        teamUserRepository.delete(recruitApplicant);
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
        Team team = this.teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("팀이 없습니다"));
        if (teamUserRepository.existsByUserIdAndTeamId(user.getId(), teamId)) {
            return team.getTeamUsers().stream().map(TeamMemberDto::new)
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
    public Team createTeam(User user, RecruitCreateRequest request) {
        Team team = Team.builder()
            .name(request.getName())
            .type(TeamType.valueOf(request.getType()))
            .dueTo(request.getDue())
            .operationFormat(TeamOperationFormat.valueOf(request.getPlace()))
            .status(TeamStatus.RECRUITING)
            .teamMemberStatus(TeamMemberStatus.RECRUITING)
            .isLock(false)
            .region1(request.getRegion().get(0))
            .region2(request.getRegion().get(1))
            .region3(null)
            .build();
        if (request.getRoleList() != null)
            addRolesToTeam(team, request.getRoleList());
        teamRepository.save(team);
        // 리더 추가
        TeamUser teamUser = TeamUser.builder()
            .teamId(team.getId())
            .userId(user.getId())
            .role(TeamUserRoleType.LEADER)
            .build();
        if (request.getLeaderJob() != null)
            teamUser.addJob(teamJobRepository.findByName(request.getLeaderJob())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 역할입니다.")));
        teamUserRepository.save(teamUser);
        return team;
    }
}
