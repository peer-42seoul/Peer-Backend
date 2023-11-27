package peer.backend.dto.board.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequest {
    private Long boardId;
    private String content;
    private String title;
    private String image;
}
