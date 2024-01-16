package peer.backend.dto.board.team;

import lombok.*;
import peer.backend.dto.tag.TagResponse;
import peer.backend.dto.user.UserShowcaseResponse;
import peer.backend.entity.board.team.Post;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ShowcaseResponse {
    private boolean author;
    private String image;
    private boolean favorite;
    private boolean liked;
    private int likeCount;
    private String name;
    private String start;
    private String end;
    private List<TagResponse> skills;
    private List<UserShowcaseResponse> member;
    private List<PostLinkResponse> links;
    private String content;
}
