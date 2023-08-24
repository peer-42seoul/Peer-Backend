package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.entity.team.Team;

@Getter
public class TeamListResponse {

    public TeamListResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.dueTo = team.getDueTo();
        this.status = team.getStatus().getValue();
    }

    private final Long id;
    private final String name;
    private final String dueTo;
    private final String status;
    // TODO: 팀원 권한 추가 예정

}
