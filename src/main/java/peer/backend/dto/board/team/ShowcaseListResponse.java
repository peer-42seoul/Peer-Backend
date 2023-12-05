package peer.backend.dto.board.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.board.recruit.TagListResponse;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShowcaseListResponse {
    private Long id;
    private String image;
    private String name;
    private String description;
    private List<TagListResponse> skill;
    private int like;
    private boolean isLiked;
    private boolean isFavorite;
    private String teamLogo;
    private String start;
    private String end;
}
