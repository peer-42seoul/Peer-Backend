package peer.backend.dto.team;

import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.team.Team;

@Getter
@NoArgsConstructor
public class TeamListResponse {

    public TeamListResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.dueTo = team.getDueTo();
        this.status = team.getStatus().getValue();
    }

    private Long id;
    private String name;
    private String dueTo;
    private String status;
    // TODO: 팀원 권한 추가 예정

}
