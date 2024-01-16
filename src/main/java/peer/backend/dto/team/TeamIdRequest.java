package peer.backend.dto.team;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TeamIdRequest {

    @NotNull(message = "team id는 필수입니다!")
    private Long teamId;
}
