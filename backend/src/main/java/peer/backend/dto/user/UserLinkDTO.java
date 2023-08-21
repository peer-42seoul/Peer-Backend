package peer.backend.dto.user;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserLink;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLinkDTO {

    private Long id;
    private String linkName;
    private String linkUrl;
    private String faviconPath;

    public static UserLinkDTO toDTO(UserLink userLink) {
        UserLinkDTO userLinkDTO = new UserLinkDTO(
            userLink.getId(),
            userLink.getLinkName(),
            userLink.getLinkUrl(),
            userLink.getFaviconPath()
        );
        return userLinkDTO;
    }
}
