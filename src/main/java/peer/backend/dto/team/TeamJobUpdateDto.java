package peer.backend.dto.team;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamJobUpdateDto {

    @NotNull
    private TeamJobRequestDto job;
}
