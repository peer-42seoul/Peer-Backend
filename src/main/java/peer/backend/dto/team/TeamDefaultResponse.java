package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.dto.AlarmResponseContainable;
import peer.backend.dto.user.UserDefaultResponse;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.team.enums.TeamUserRoleType;

@Getter
public class TeamDefaultResponse implements AlarmResponseContainable {

    private final Long teamId;
    private final TeamType teamType;
    private final String name;
    private final TeamStatus teamStatus;
    private UserDefaultResponse leader;

    public TeamDefaultResponse(Team team) {
        this.teamId = team.getId();
        this.teamType = team.getType();
        this.name = team.getName();
        this.teamStatus = team.getStatus();
        for (TeamUser tu : team.getTeamUsers()) {
            if (tu.getRole().equals(TeamUserRoleType.LEADER)) {
                this.leader = new UserDefaultResponse(tu.getUser());
                break;
            }
        }
    }
}
