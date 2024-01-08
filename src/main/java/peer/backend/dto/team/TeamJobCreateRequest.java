package peer.backend.dto.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
public class TeamJobCreateRequest {
    @NotNull
    private TeamJobRequestDto job;
}
