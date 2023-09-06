package peer.backend.dto.security.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ToReissueTokens {
    private String expiredaccessToken;
    private String refreshToken;
}
