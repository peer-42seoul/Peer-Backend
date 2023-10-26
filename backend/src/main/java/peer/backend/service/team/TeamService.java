package peer.backend.service.team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.dto.board.recruit.RecruitAnswerDto;
import peer.backend.dto.team.*;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitApplicant;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.user.User;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.board.recruit.RecruitApplicantRepository;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final RecruitRepository recruitRepository;
    private final RecruitApplicantRepository recruitApplicantRepository;

    @Transactional
    public List<TeamListResponse> getTeamList(TeamStatus teamStatus, User user) {
        if (teamStatus == null) {
            throw new NotFoundException("존재하지 않는 팀 상태 입니다.");
        }
        List<TeamUser> teamUserList = teamUserRepository.findByUserId(user.getId());
        List<TeamListResponse> teamListResponse = new ArrayList<>();
        for (TeamUser teamUser : teamUserList) {
            Team team = teamUser.getTeam();
            if (teamUser.getTeam().getStatus() == teamStatus)
                teamListResponse.add(new TeamListResponse(team, teamUser, team.getTeamUsers().size()));
        }
        return  teamListResponse;
    }

    @Transactional
    public TeamSettingDto getTeamSetting(Long teamId, String email) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        TeamUser teamUser = getTeamUserByName(teamId, email);
        return new TeamSettingDto(team);
    }

    @Transactional
    public void updateTeamSetting(Long teamId, TeamSettingInfoDto teamSettingInfoDto, String userName) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (teamId.equals(Long.parseLong(teamSettingInfoDto.getId())) &&
            getTeamUserByName(teamId, userName).getRole() == TeamUserRoleType.LEADER) {
            team.update(teamSettingInfoDto);
        } else {
            throw new ForbiddenException("팀장이 아니거나 팀 아이디가 일치하지 않습니다.");
        }
    }

    @Transactional
    public ArrayList<TeamMemberDto> deleteTeamMember(Long teamId, String deletingToUserId, String userName) {
        if (deletingToUserId.equals(userRepository.findByName(userName).orElseThrow(() -> new NotFoundException("존재하지 않는 유저 아이디 입니다.")).getId().toString())) {
            throw new ForbiddenException("자기 자신을 팀에서 추방할 수 없습니다.");
        }
        TeamUser teamUser = getTeamUserByName(teamId, userName);
        Team team = teamUser.getTeam();
        boolean isRemoved = team.deleteTeamUser(Long.parseLong(deletingToUserId));
        if (!isRemoved) {
            throw new ForbiddenException("삭제할 유저는 팀장이 아닙니다!");
        }
        return team.getTeamUsers().stream().map(TeamMemberDto::new).collect(Collectors.toCollection(ArrayList::new));
    }

    @Transactional
    public void grantRole(Long teamId, Long grantingUserId, String userName, TeamUserRoleType teamUserRoleType) {
        TeamUser teamUser = getTeamUserByName(teamId, userName);
        Team team = teamUser.getTeam();
        team.grantLeaderPermission(grantingUserId, teamUserRoleType);
    }

    @Transactional
    public void exitTeam(Long teamId, String userName) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        TeamUser teamUser = getTeamUserByName(teamId, userName);
        if (!team.deleteTeamUser(teamUser.getUserId())) {
            throw new NotFoundException("탈퇴할 수 없습니다.");
        } else {
            if (team.getTeamUsers().size() == 0) {
                teamRepository.delete(team);
            } else {
                team.grantLeaderPermission(team.getTeamUsers().get(0).getUserId(), TeamUserRoleType.LEADER);
            }
        }
    }

    @Transactional
    public List<TeamApplicantListDto> getTeamApplicantList(Long teamId, User user) {
        TeamUser teamUser = getTeamUserByName(teamId, user.getName());
        List<RecruitApplicant> recruitApplicantList = recruitApplicantRepository.findByRecruitId(teamId);
        List<TeamApplicantListDto> result = new ArrayList<>();
        Recruit recruit = recruitRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (recruit.getStatus() != RecruitStatus.ONGOING) {
            throw new NotFoundException("모집이 진행중이 아닙니다.");
        }
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
                    .name(recruitApplicant.getNickname())
                    .userId(recruitApplicant.getUserId())
                    .build());
        }
        return result;
    }

    @Transactional
    public void acceptTeamApplicant(Long teamId, Long applicantId, User user) {
        TeamUser teamUser = getTeamUserByName(teamId, user.getName());
        RecruitApplicant recruitApplicant = recruitApplicantRepository.findByUserIdAndRecruitId(applicantId, teamId);
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
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
        TeamUser teamUser = getTeamUserByName(teamId, user.getName());
        RecruitApplicant recruitApplicant = recruitApplicantRepository.findByUserIdAndRecruitId(applicantId, teamId);
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (recruitApplicant == null) {
            throw new NotFoundException("존재하지 않는 지원자입니다.");
        }
        recruitApplicantRepository.delete(recruitApplicant);
        //TODO: 신청자에게 알림을 보내야됨
    }

    @Transactional
    public TeamInfoResponse getTeamInfo(Long teamId, User user) {
        Team team = this.teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("팀이 없습니다"));
        if (teamUserRepository.existsByUserIdAndTeamId(user.getId(), teamId)) {
            return new TeamInfoResponse(team);
        }
        else {
            throw new ForbiddenException("팀에 속해있지 않습니다.");
        }
    }

    @Transactional
    public List<TeamMemberDto> getTeamMemberList(Long teamId, User user) {
        Team team = this.teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("팀이 없습니다"));
        if (teamUserRepository.existsByUserIdAndTeamId(user.getId(), teamId)) {
            return team.getTeamUsers().stream().map(TeamMemberDto::new).collect(Collectors.toList());
        }
        else {
            throw new ForbiddenException("팀에 속해있지 않습니다.");
        }
    }

    private TeamUser getTeamUserByName(Long teamId, String userName) throws NotFoundException, ForbiddenException {
        TeamUser teamUser = this.teamUserRepository.findByUserIdAndTeamId(this.userRepository.findByName(userName).orElseThrow(() -> new NotFoundException("존재하지 않는 유저 아이디 입니다.")).getId(), teamId);
        if (teamUser.getRole() != TeamUserRoleType.LEADER) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        return teamUser;
    }
}
