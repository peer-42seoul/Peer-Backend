package peer.backend.dto.profile.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserLinkDTO {
    private String linkName;
    private String linkUrl;
}
