package peer.backend.config.jwt;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import peer.backend.entity.user.User;
import peer.backend.repository.user.RefreshTokenRepository;
import peer.backend.service.UserDetailsServiceImpl;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("token provider Test")
public class TokenProviderTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @InjectMocks
    private TokenProvider tokenProvider;

    @Value("${jwt.token.secret}")
    String secretKey;

    User user;
    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .user_id("asdf").name("asdf").nickname("asdf")
                .password("asdf").email("asdf@asdf.com").birthday(LocalDateTime.now()).phone("010-1234-1234").address("asdf")
                .is_alarm(false).certification(false)
                .imageUrl(null).company(null).introduce(null).representAchievement(null)
                .peerLevel(null).peerOperation(null)
                .userPushKeywords(null).userAchievements(null).userLinks(null)
                .build();
    }

    @Test
    @DisplayName("create token test")
    void createToken() {
        String accesstoken = tokenProvider.createAccessToken(user);
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] rowToken = accesstoken.split("//.");
        String header = new String(decoder.decode(rowToken[0]));
        String payload = new String(decoder.decode(rowToken[1]));
        String vert = new String(decoder.decode(rowToken[2]));

        System.out.printf("%s\n%s\n%s\n%n", header, payload, vert);
    }
}
