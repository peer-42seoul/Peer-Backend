package peer.backend.dto.board.team;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.tag.TagResponse;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowcaseListResponse {

    private Long id;
    private String image;
    private String name;
    private String description;
    private List<TagResponse> skill;
    private int like;
    private boolean isLiked;
    private boolean isFavorite;
    private String teamLogo;
    private String start;
    private String end;
}
