package peer.backend.dto.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TeamJobUpdateDto {
    List<TeamJobRequestDto> job;
}
