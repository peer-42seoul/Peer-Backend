package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamUserStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TeamSettingDto {
    private TeamSettingInfoDto team;
    private List<TeamJobSettingDto> job;
    private ArrayList<TeamMemberDto> member;

    public TeamSettingDto(Team team, List<TeamUser> teamUserList) {
        this.team = new TeamSettingInfoDto(team);
        this.job = team.getJobs().stream().map(TeamJobSettingDto::new).collect(Collectors.toList());
        this.member = new ArrayList<>();
        for (TeamUser teamUser: teamUserList) {
            if (teamUser.getStatus().equals(TeamUserStatus.APPROVED))
                this.member.add(new TeamMemberDto(teamUser));
        }
    }
}
