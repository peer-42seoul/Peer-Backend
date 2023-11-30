package peer.backend.dto.security.response;

import lombok.Getter;

@Getter
public class JwtDto {
    private final Long userId;
    private final String accessToken;
    private final String refreshToken;

    public JwtDto(Long userId, String accessToken, String refreshToken) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
