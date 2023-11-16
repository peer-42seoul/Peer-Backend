package peer.backend.service.team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.TeamCreateTracking;
import peer.backend.annotation.tracking.TeamUpdateTracking;
import peer.backend.dto.board.recruit.RecruitAnswerDto;
import peer.backend.dto.board.recruit.RecruitCreateRequest;
import peer.backend.dto.team.TeamApplicantListDto;
import peer.backend.dto.team.TeamImageDto;
import peer.backend.dto.team.TeamInfoResponse;
import peer.backend.dto.team.TeamListResponse;
import peer.backend.dto.team.TeamMemberDto;
import peer.backend.dto.team.TeamSettingDto;
import peer.backend.dto.team.TeamSettingInfoDto;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitApplicant;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.user.User;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.recruit.RecruitApplicantRepository;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.file.FileService;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final RecruitRepository recruitRepository;
    private final RecruitApplicantRepository recruitApplicantRepository;
    private final FileService fileService;
    @Value("${custom.filePath}")
    private String filePath;

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
        return new TeamSettingDto(team);
    }

    @Transactional
    @TeamUpdateTracking
    public void updateTeamSetting(Long teamId, TeamSettingInfoDto teamSettingInfoDto, User user) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (teamId.equals(Long.parseLong(teamSettingInfoDto.getId())) &&
            isLeader(teamId, user)) {
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

    //TODO: Auth 해야됨
    @Transactional
    public void grantRole(Long teamId, Long grantingUserId, User user,
        TeamUserRoleType teamUserRoleType) {
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
            if (team.getTeamUsers().size() == 0) {
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
        List<RecruitApplicant> recruitApplicantList = recruitApplicantRepository.findByRecruitId(
            teamId);
        List<TeamApplicantListDto> result = new ArrayList<>();
        Recruit recruit = recruitRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (recruit.getStatus() != RecruitStatus.ONGOING) {
            throw new NotFoundException("모집이 진행중이 아닙니다.");
        }
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
                .name(recruitApplicant.getNickname())
                .userId(recruitApplicant.getUserId())
                .build());
        }
        return result;
    }

    @Transactional
    public void acceptTeamApplicant(Long teamId, Long applicantId, User user) {
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        RecruitApplicant recruitApplicant = recruitApplicantRepository.findByUserIdAndRecruitId(
            applicantId, teamId);
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (recruitApplicant == null) {
            throw new NotFoundException("존재하지 않는 지원자입니다.");
        }
        TeamUser newTeamUser = TeamUser.builder()
            .teamId(teamId)
            .userId(applicantId)
            .role(TeamUserRoleType.MEMBER)
            .review("")
            .build();
        teamUserRepository.save(newTeamUser);
        recruitApplicantRepository.delete(recruitApplicant);
        //TODO: 신청자에게 알림을 보내야됨
    }

    @Transactional
    public void rejectTeamApplicant(Long teamId, Long applicantId, User user) {
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        RecruitApplicant recruitApplicant = recruitApplicantRepository.findByUserIdAndRecruitId(
            applicantId, teamId);
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (recruitApplicant == null) {
            throw new NotFoundException("존재하지 않는 지원자입니다.");
        }
        recruitApplicantRepository.delete(recruitApplicant);
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

    @Transactional
    public void deleteTeamImage(Long teamId, User user) throws IOException {
        Team team = this.teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("팀이 없습니다"));
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        fileService.deleteFile(team.getTeamLogoPath());
    }

    @Transactional
    public void updateTeamImage(Long teamId, TeamImageDto teamImageDto, User user)
        throws IOException {
        String newFilePath;
        Team team = this.teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException("팀이 없습니다"));
        if (!isLeader(teamId, user)) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        String oldFilePath = team.getTeamLogoPath();
        if (oldFilePath == null) {
            newFilePath = fileService.saveFile(teamImageDto.getTeamImage(), filePath, "image");
        } else {
            newFilePath = fileService.updateFile(teamImageDto.getTeamImage(), oldFilePath, "image");
        }
        team.addImage(newFilePath);
    }

    private TeamUser getTeamUserByName(Long teamId, String userName)
        throws NotFoundException, ForbiddenException {
        TeamUser teamUser = this.teamUserRepository.findByUserIdAndTeamId(
            this.userRepository.findByName(userName)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저 아이디 입니다.")).getId(), teamId);
        if (teamUser.getRole() != TeamUserRoleType.LEADER) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        return teamUser;
    }

    private boolean isLeader(Long teamId, User user) {
        return teamUserRepository.findTeamUserRoleTypeByTeamIdAndUserId(teamId, user.getId())
            == TeamUserRoleType.LEADER;
    }

    @TeamCreateTracking
    @Transactional
    public Team createTeam(User user, RecruitCreateRequest request) {
        System.out.println(TeamType.valueOf(request.getType()));
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
}
