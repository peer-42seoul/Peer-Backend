package peer.backend.dto.privateinfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PrivateDataDTO {
    private String token;
    private String code;
}
