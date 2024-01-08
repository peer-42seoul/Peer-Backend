package peer.backend.dto.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class TeamJobUpdateDto {
    List<TeamJobRequestDto> job;
}
