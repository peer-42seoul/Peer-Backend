package peer.backend.dto.board.recruit;


import lombok.*;
import peer.backend.dto.tag.TagResponse;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.TagListManager;
import peer.backend.entity.tag.Tag;

import java.util.List;
import java.util.stream.Collectors;

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
