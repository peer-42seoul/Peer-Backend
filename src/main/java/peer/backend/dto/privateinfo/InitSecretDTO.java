package peer.backend.dto.privateinfo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InitSecretDTO {
    private String secret;
    private Long code;
}
