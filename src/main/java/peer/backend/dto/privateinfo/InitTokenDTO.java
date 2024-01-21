package peer.backend.dto.privateinfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InitTokenDTO {
    private String token;
    private Long code;
}
