package peer.backend.dto.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamListResponseDto {

    public TeamListResponseDto(Team team) {
        this.name = team.getName();
        this.dueTo = team.getDueTo();
        this.status = team.getStatus().getValue();
    }

    String name;
    String dueTo;
    String status;
    /*
        To-Do: 팀원 권한 추가 예정
     */

}
