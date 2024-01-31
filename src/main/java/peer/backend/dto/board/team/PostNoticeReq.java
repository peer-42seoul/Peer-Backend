package peer.backend.dto.board.team;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostNoticeReq {
    private Long teamId;
    private String content;
    private String title;
    private String image;
}
