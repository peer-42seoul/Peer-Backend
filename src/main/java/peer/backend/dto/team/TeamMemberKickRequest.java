package peer.backend.dto.team;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class TeamMemberKickRequest {

    @NotNull
    private Long teamId;

    @NotNull
    private Long userId;
}

