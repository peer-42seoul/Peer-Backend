package peer.backend.dto.board.recruit;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.dto.tag.TagResponse;

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
    private List<TagResponse> tagList;
    private Long recruit_id;
    private boolean favorite;
}
