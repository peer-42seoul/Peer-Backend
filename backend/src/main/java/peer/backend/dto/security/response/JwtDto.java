package peer.backend.dto.security.response;

import lombok.Getter;

@Getter
public class JwtDto {
    private final String accessToken;
    private final String refreshToken;

    public JwtDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
