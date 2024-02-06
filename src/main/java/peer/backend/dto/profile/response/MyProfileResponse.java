package peer.backend.dto.profile.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import peer.backend.dto.profile.SkillDTO;
import peer.backend.dto.profile.request.UserLinkRequest;
import peer.backend.entity.tag.Tag;

@Getter
@Builder
public class MyProfileResponse {
    private Long id;
    private String profileImageUrl;
    private String nickname;
    private String email;
    private String association;
    private String introduction;
    private List<UserLinkResponse> linkList;
    private List<SkillDTO> skillList;
    private boolean portfolioVisibility;
}
