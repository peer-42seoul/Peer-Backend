package peer.backend.config.jwt;

import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import peer.backend.entity.user.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("token provider Test")
public class TokenProviderTest {

    @InjectMocks
    private TokenProvider tokenProvider;

    @Value("${jwt.token.secret}")
    String secretKey;

    User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
            .id(1L)
            .name("asdf").nickname("asdf")
            .password("asdf").email("asdf@asdf.com").address("asdf")
            .isAlarm(false).certification(false)
            .imageUrl(null).company(null).introduce(null).representAchievement(null)
            .build();

        ReflectionTestUtils.setField(tokenProvider, "secretKey",
            "testtesttesttesttesttesttesttesttesttesttesttesttest");
    }

    @Test
    @DisplayName("create token test")
    void createToken() {
        String accesstoken = tokenProvider.createAccessToken(user);
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] rowToken = accesstoken.split("\\.");
        String header = new String(decoder.decode(rowToken[0]));
        String payload = new String(decoder.decode(rowToken[1]));
        String vert = new String(decoder.decode(rowToken[2]));

        System.out.printf("%s\n%s\n%s\n%n", header, payload, vert);
    }
}
