package peer.backend.dto.team;

import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamUserRoleType;

@Getter
@NoArgsConstructor
public class TeamListResponse {

    public TeamListResponse(Team team, TeamUserRoleType teamUserRoleType) {
        this.id = team.getId();
        this.name = team.getName();
        this.dueTo = team.getDueTo();
        this.status = team.getStatus().getValue();
        this.myRole = teamUserRoleType.getValue();
        this.region = team.getRegion1();
        this.operationFormat = team.getOperationFormat().getValue();
    }

    private Long id;
    private String name;
    private String dueTo;
    private String status;
    private String myRole;
    private String region;
    private String operationFormat;
    // TODO: 팀원 권한 추가 예정
}
