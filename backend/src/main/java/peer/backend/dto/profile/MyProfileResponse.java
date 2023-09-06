package peer.backend.dto.profile;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import peer.backend.entity.user.UserAchievement;
import peer.backend.entity.user.UserLink;

@Getter
@Builder
public class MyProfileResponse {
    private Long id;
    private String profileImageUrl;
    private String introduction;
    private List<UserLink> linkList;
    private String phone;
    private String representAchievement;
    private List<UserAchievement> achievements;
}
