package peer.backend.dto.privateinfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PrivateTokenDTO {
    private String token;
    private String key;
}
