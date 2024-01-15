package peer.backend.dto.board.team;

import lombok.Getter;
import peer.backend.dto.profile.response.UserLinkResponse;
import peer.backend.dto.tag.TagResponse;
import peer.backend.dto.user.UserShowcaseResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ShowcaseWriteResponse {
    private String title;
    private List<TagResponse> skills;
    private LocalDateTime start;
    private LocalDateTime end;
    private List<UserShowcaseResponse> memberList;
    private List<UserLinkResponse>
}
