package peer.backend.dto.team;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class SimpleBoardRes {
    private Long boardId;
    private String boardName;

    public SimpleBoardRes(Long boardId, String boardName) {
        this.boardId = boardId;
        this.boardName = boardName;
    }
}
