package peer.backend.service;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.dto.security.Message;
import peer.backend.dto.security.response.JwtDto;
import peer.backend.entity.user.RefreshToken;
import peer.backend.entity.user.User;
import peer.backend.repository.user.TokenRepository;
import peer.backend.repository.user.UserRepository;
import scala.util.parsing.json.JSON;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
// @SpringbootTest와 함께 사용하면 충돌. 그리고 Moekito를 사용할 거면 SpringbootTest는 성능상 좋지 않음
@DisplayName("login service Test")
public class LoginServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private TokenProvider tokenProvider;
    @InjectMocks
    private LoginService service;

    Long id;
    String refreshToken;
    String email;
    String password;
    Optional<User> optionalUser;
    Optional<RefreshToken> optionalRefreshToken;

    @BeforeEach
    void beforeEach() {
        id = 1L;
        refreshToken = "eyJ0eXAiOiJyZWZyZXNoVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsImlhdCI6MTY5MzgxMDc3OCwiZXhwIjoxNjk0NDE1NTc4fQ.fQ6TDUTiCa1HWc_Su00wmbbZ4wUnkpUctIesMVM3jdI";

        email = "test@test.com";
        password = "test";

        User user = User.builder()
            .id(id)
            .userId("test").name("test").nickname("test")
            .password(password).email(email).birthday(LocalDate.now())
            .phone("010-1234-1234").address("test")
            .isAlarm(false).certification(false)
            .imageUrl(null).company(null).introduce(null).representAchievement(null)
            .peerLevel(null).peerOperation(null)
            .userPushKeywords(null).userAchievements(null).userLinks(null)
            .build();
        optionalUser = Optional.of(user);

        RefreshToken token = RefreshToken.builder()
            .userId(id)
            .refreshToken(refreshToken)
            .build();
        optionalRefreshToken = Optional.of(token);
    }

    @Test
    @DisplayName("test to reissue access token")
    void reissueAccessToken() {

        when(userRepository.findById(anyLong())).thenReturn(optionalUser);
        when(tokenRepository.findById(anyLong())).thenReturn(optionalRefreshToken);
        when(tokenProvider.validateRefreshToken(anyString())).thenReturn(false);

        Message result = service.reissue(id, refreshToken);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
    }
}
