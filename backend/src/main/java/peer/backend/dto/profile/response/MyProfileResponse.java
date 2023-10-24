package peer.backend.dto.profile.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import peer.backend.dto.profile.request.UserLinkRequest;

@Getter
@Builder
public class MyProfileResponse {
    private String profileImageUrl;
    private String nickname;
    private String email;
    private String company;
    private String introduction;
    private List<UserLinkRequest> linkList;
}
