package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.entity.team.Team;

@Getter
public class TeamListResponseDto {

    public TeamListResponseDto(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.dueTo = team.getDueTo();
        this.status = team.getStatus().getValue();
    }

    private Long id;
    private String name;
    private String dueTo;
    private String status;
    /*
        To-Do: 팀원 권한 추가 예정
     */

}
