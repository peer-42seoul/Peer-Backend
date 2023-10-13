package peer.backend.dto.profile;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import peer.backend.entity.user.UserAchievement;
import peer.backend.entity.user.UserLink;

@Getter
@Builder
public class MyProfileResponse {
    private String profileImageUrl;
    private String nickname;
    private String email;
    private String company;
    private String introduction;
    private List<UserLinkDTO> linkList;
}
