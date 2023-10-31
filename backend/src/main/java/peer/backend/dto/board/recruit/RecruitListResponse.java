package peer.backend.dto.board.recruit;

import lombok.*;
import peer.backend.entity.board.recruit.enums.RecruitStatus;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruitListResponse {
    private String title;
    private String image;
    private Long user_id;
    private String user_nickname;
    private String user_thumbnail;
    private String status;
    private List<String> tagList;
    private boolean isFavorite;
}
