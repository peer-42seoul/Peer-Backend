package peer.backend.service.team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.dto.team.*;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.user.User;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;

    @Transactional
    public List<TeamListResponse> getTeamList(Long userId, TeamStatus teamStatus) {
        if (teamStatus == null) {
            throw new NotFoundException("존재하지 않는 팀 상태 입니다.");
        }
        User user = this.userRepository.findById(userId).orElseThrow(() -> new NotFoundException("존재하지 않는 유저 아이디 입니다."));
        List<Team> teamList = user.getTeamUsers().stream().map(TeamUser::getTeam).collect(Collectors.toList());
        List<TeamListResponse> teamListResponse = new ArrayList<>();
        for (Team team : teamList) {
            if (team.getStatus() == teamStatus)
                teamListResponse.add(new TeamListResponse(team, teamUserRepository.findByUserIdAndTeamId(userId, team.getId())));
        }
        return  teamListResponse;
    }

//    @Transactional
//    public TeamInfoResponse getTeamInfo(Long teamId, String userEmail) {
//        Long userId = this.userRepository.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("존재하지 않는 유저 아이디 입니다.")).getId();
//        if (teamUserRepository.existsByUserIdAndTeamId(userId, teamId)) {
//            Team team = this.teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀 아이디 입니다."));
//            return new TeamInfoResponse(team);
//        }
//        else {
//            throw new NotFoundException("해당 팀에 접근할 수 없는 유저입니다.");
//        }
//    }

    @Transactional
    public TeamSettingDto getTeamSetting(Long teamId, String email) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        TeamUser teamUser = getTeamUserByEmail(teamId, email);
        return new TeamSettingDto(team);
    }

    @Transactional
    public void updateTeamSetting(Long teamId, TeamSettingInfoDto teamSettingInfoDto, String userEmail) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        if (teamId.equals(Long.parseLong(teamSettingInfoDto.getId())) &&
            getTeamUserByEmail(teamId, userEmail).getTeamUserRoleType() == TeamUserRoleType.LEADER) {
            team.update(teamSettingInfoDto);
        } else {
            throw new ForbiddenException("팀장이 아니거나 팀 아이디가 일치하지 않습니다.");
        }
    }

    @Transactional
    public ArrayList<TeamMemberDto> deleteTeamMember(Long teamId, String deletingToUserId, String userEmail) {
        if (deletingToUserId.equals(userRepository.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("존재하지 않는 유저 아이디 입니다.")).getId().toString())) {
            throw new ForbiddenException("자기 자신을 팀에서 추방할 수 없습니다.");
        }
        TeamUser teamUser = getTeamUserByEmail(teamId, userEmail);
        Team team = teamUser.getTeam();
        boolean isRemoved = team.deleteTeamUser(Long.parseLong(deletingToUserId));
        if (!isRemoved) {
            throw new ForbiddenException("삭제할 유저는 팀장이 아닙니다!");
        }
        return team.getTeamUsers().stream().map(TeamMemberDto::new).collect(Collectors.toCollection(ArrayList::new));
    }

    @Transactional
    public void grantRole(Long teamId, Long grantingUserId, String userEmail, TeamUserRoleType teamUserRoleType) {
        TeamUser teamUser = getTeamUserByEmail(teamId, userEmail);
        Team team = teamUser.getTeam();
        team.grantLeaderPermission(grantingUserId, teamUserRoleType);
    }

    @Transactional
    public void exitTeam(Long teamId, String userEmail) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new NotFoundException("존재하지 않는 팀입니다."));
        TeamUser teamUser = getTeamUserByEmail(teamId, userEmail);
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

//    @Transactional
//    public Team getTeamById(Long teamId) {
//        return this.teamRepository.findById(teamId)
//            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀 아이디 입니다."));
//    }
//
//    @Transactional
//    public Team getTeamByName(String teamName) {
//        return this.teamRepository.findByName(teamName)
//            .orElseThrow(() -> new NotFoundException("존재하지 않는 팀 아이디 입니다."));
//    }
//
//    @Transactional
//    public void updateTeam(Long teamId, UpdateTeamRequest request) {
//        Team team = this.getTeamById(teamId);
//        team.update(request);
//    }
////
//    @Transactional
//    public void deleteTeamUser(Long teamId, Long userId) {
//        if (!this.teamUserRepository.existsByUserIdAndTeamId(userId, teamId)) {
//            throw new NotFoundException("해당 유저는 팀원이 아닙니다!");
//        }
//        this.teamUserRepository.deleteByUserIdAndTeamId(userId, teamId);
//    }

    private TeamUser getTeamUserByEmail(Long teamId, String email) throws NotFoundException, ForbiddenException {
        TeamUser teamUser = this.teamUserRepository.findByUserIdAndTeamId(this.userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("존재하지 않는 유저 아이디 입니다.")).getId(), teamId);
        if (teamUser.getTeamUserRoleType() != TeamUserRoleType.LEADER) {
            throw new ForbiddenException("팀장이 아닙니다.");
        }
        return teamUser;
    }

}
