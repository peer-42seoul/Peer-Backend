package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TeamSettingDto {
    private TeamSettingInfoDto team;
    private ArrayList<TeamMemberDto> member;

    public TeamSettingDto(Team team, List<TeamUser> teamUserList) {
        this.team = new TeamSettingInfoDto(team);
        this.member = new ArrayList<>();
        for (TeamUser teamUser: teamUserList) {
            this.member.add(new TeamMemberDto(teamUser));
        }
    }
}
