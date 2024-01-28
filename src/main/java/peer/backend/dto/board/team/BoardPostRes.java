package peer.backend.dto.board.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.board.team.Post;

@Getter
@RequiredArgsConstructor
public class BoardPostRes {
    Long boardId;
    Long postId;

    public BoardPostRes(Long boardId, Long postId) {
        this.boardId = boardId;
        this.postId = postId;
    }
    public static BoardPostRes from(Post post) {
        return new BoardPostRes(post.getBoard().getId(), post.getId());
    }
}
