package peer.backend.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.board.recruit.TagListResponse;

import java.time.LocalDateTime;
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
    private boolean is_liked;
    private boolean is_favorite;
    private String team_logo;
    private LocalDateTime start;
    private LocalDateTime end;
}
