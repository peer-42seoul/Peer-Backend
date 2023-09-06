package peer.backend.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserLinkDTO {
    private Long id;
    private String linkName;
    private String linkUrl;
    private String faviconPath;
}
