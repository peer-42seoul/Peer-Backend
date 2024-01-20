package peer.backend.dto.privateInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InitTokenDTO {
    private String token;
    private long code;
}
