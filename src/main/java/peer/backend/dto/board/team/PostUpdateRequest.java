package peer.backend.dto.board.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostUpdateRequest {
    private String content;
    private String title;
    private String image;
}
