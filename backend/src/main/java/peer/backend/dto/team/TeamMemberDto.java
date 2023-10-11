package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.entity.team.TeamUser;

@Getter
public class TeamMemberDto {
    private Long id;
    private String name;
    private String role;

    public TeamMemberDto(TeamUser teamUser) {
        this.id = teamUser.getUserId();
        this.name = teamUser.getUser().getName();
        this.role = teamUser.getTeamUserRoleType().getValue();
    }
}
