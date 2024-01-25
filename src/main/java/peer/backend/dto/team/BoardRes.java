package peer.backend.dto.team;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class BoardRes {
    private Long boardId;
    private String boardName;
    private List<PostRes> posts;

    public BoardRes(Long boardId, String boardName, List<PostRes> posts) {
        this.boardId = boardId;
        this.boardName = boardName;
        this.posts = posts;
    }
}
