package peer.backend.dto.profile;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import peer.backend.entity.user.UserLink;

@Getter
@Builder
public class EditProfileDTO {
    private String profileImageUrl;
    private String introduction;
    private List<UserLink> linkList;
    private String phone;
    private String representAchievement;
    private String achievement;
}
