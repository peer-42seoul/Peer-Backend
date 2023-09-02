package peer.backend.dto.profile;

import java.util.ArrayList;
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

    public static List<UserLinkDTO> toUserLinkDTO(List<UserLink> userLinks)
    {
        List<UserLinkDTO> userLinkDTOS = new ArrayList<>();
        for (UserLink userLink : userLinks)
        {
            UserLinkDTO userLinkDTO = UserLinkDTO.builder()
                .id(userLink.getId())
                .linkName(userLink.getLinkName())
                .linkUrl(userLink.getLinkUrl())
                .faviconPath(userLink.getFaviconPath())
                .build();
            userLinkDTOS.add(userLinkDTO);
        }
        return userLinkDTOS;
    }
}
