package peer.backend.dto.board.team;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.dto.profile.response.UserLinkResponse;
import peer.backend.dto.tag.TagResponse;
import peer.backend.dto.user.UserShowcaseResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class ShowcaseResponse {
    private boolean author;
    private String image;
    private boolean favorite;
    private boolean liked;
    private int likeCount;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private List<TagResponse> skills;
    private List<UserShowcaseResponse> member;
    private List<UserLinkResponse> links;
    private String content;
}
