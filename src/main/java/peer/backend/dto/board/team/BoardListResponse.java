package peer.backend.dto.board.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoardListResponse {
    private Long boardId;
    private String name;
}
