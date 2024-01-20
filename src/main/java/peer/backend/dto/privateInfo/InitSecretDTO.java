package peer.backend.dto.privateInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class InitSecretDTO {
    private String secret;
    private Long code;
}
