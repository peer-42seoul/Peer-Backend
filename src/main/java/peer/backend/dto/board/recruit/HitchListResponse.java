package peer.backend.dto.board.recruit;


import lombok.*;
import peer.backend.dto.tag.TagResponse;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class HitchListResponse {
    private String teamName;
    private String title;
    private Long recruitId;
    private List<TagResponse> tagList;
    private String image;
}
