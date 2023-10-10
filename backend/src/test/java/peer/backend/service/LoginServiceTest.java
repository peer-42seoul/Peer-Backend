package peer.backend.service;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.dto.security.Message;
import peer.backend.entity.user.User;
import peer.backend.repository.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private TokenProvider tokenProvider;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private LoginService service;

    Long id;
    String refreshToken;
    String email;
    String password;
    Optional<User> optionalUser;

    @BeforeEach
    void beforeEach() {
        id = 1L;
        refreshToken = "eyJ0eXAiOiJyZWZyZXNoVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsImlhdCI6MTY5MzgxMDc3OCwiZXhwIjoxNjk0NDE1NTc4fQ.fQ6TDUTiCa1HWc_Su00wmbbZ4wUnkpUctIesMVM3jdI";

        email = "test@test.com";
        password = "test";

        User user = User.builder()
            .id(id)
            .name("test").nickname("test")
            .password(password).email(email)
            .address("test")
            .isAlarm(false).certification(false)
            .imageUrl(null).company(null).introduce(null).representAchievement(null)
            .peerLevel(null).peerOperation(null)
            .userPushKeywords(null).userAchievements(null).userLinks(null)
            .build();
        optionalUser = Optional.of(user);
    }

    @Test
    @DisplayName("test to reissue access token")
    void reissueAccessToken() {

        when(userRepository.findById(anyLong())).thenReturn(optionalUser);
//        when(tokenProvider.validRefreshToken(anyString())).thenReturn(false);
        String refresh = "eyJ0eXAiOiJyZWZyZXNoVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsImlhdCI6MTY5MzgxMDc3OCwiZXhwIjoxNjk0NDE1NTc4fQ.fQ6TDUTiCa1HWc_Su00wmbbZ4wUnkpUctIesMVM3jdI";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(refresh);
        when(tokenProvider.createAccessToken(any())).thenReturn("accessToken");
        String result = service.reissue(id, refreshToken);

        assertThat(result).isNotNull();
    }
}
