package peer.backend.dto.profile.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLinkResponse {
    private Long id;
    private String linkUrl;
    private String linkName;
}
