package peer.backend.dto.privateInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PrivateTokenDTO {
    private String token;
    private String key;
}
