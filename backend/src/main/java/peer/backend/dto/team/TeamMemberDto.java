package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamUserRoleType;

@Getter
public class TeamMemberDto {
    private Long id;
    private String name;
    private TeamUserRoleType role;

    public TeamMemberDto(TeamUser teamUser) {
        this.id = teamUser.getUserId();
        this.name = teamUser.getUser().getNickname();
        this.role = teamUser.getRole();
    }
}
