package peer.backend.dto.team;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
public class TeamJobRequestDto {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private int max;
}
