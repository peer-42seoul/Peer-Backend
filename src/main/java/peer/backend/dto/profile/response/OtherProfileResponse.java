package peer.backend.dto.profile.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtherProfileResponse {
    private String profileImageUrl = null;
    private String nickname = null;
}
