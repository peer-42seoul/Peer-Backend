package peer.backend.dto.team;

import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;

@Getter
@NoArgsConstructor
public class TeamListResponse {

    public TeamListResponse(Team team, TeamUser teamUser) {
        this.id = team.getId().toString();
        this.name = team.getName();
        this.dueTo = team.getDueTo();
        this.status = team.getStatus().getValue();
        this.role = teamUser.getTeamUserRoleType().getValue();
        this.type = team.getType().getValue();
        this.teamCount = String.format(team.getTeamUsers().size() + " / " + team.getMaxMember());
        this.region = team.getRegion1();
        this.operationFormat = team.getOperationFormat().getValue();
    }

    private String id;
    private String name;
    private String dueTo;
    private String status;
    private String type;
    private String role;
    private String teamCount; //"3 / 12"
    private String region;
    private String operationFormat;
    // TODO: 팀원 권한 추가 예정
}
