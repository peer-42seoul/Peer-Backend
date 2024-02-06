package peer.backend.dto.profile.response;

import lombok.Builder;
import lombok.Getter;
import peer.backend.dto.profile.SkillDTO;

import java.util.List;

@Getter
@Builder
public class OtherProfileResponseDTO {
    private Long id;
    private String email;
    private String profileImageUrl;
    private String nickname;
    private String introduction;
    private List<UserLinkResponse> linkList;
    private List<SkillDTO> skillList;
    private boolean portfolioVisibility;
}
