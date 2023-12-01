package peer.backend.dto.board.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostListResponse {
    private Long postId;
    private String content;
    private String title;
}
