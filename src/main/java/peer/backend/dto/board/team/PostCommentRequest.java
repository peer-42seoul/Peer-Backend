package peer.backend.dto.board.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PostCommentRequest {
    private String content;
    private Long teamId;
    private Long postId;
}
