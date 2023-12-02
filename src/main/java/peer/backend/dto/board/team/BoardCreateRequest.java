package peer.backend.dto.board.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.board.team.enums.BoardType;

@Getter
@RequiredArgsConstructor
public class BoardCreateRequest {
    private Long teamId;
    private String name;
}
