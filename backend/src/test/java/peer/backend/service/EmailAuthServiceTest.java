package peer.backend.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
@DisplayName("email auth service Test")
public class EmailAuthServiceTest {


    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    EmailAuthService emailAuthService;

    @Test
    @DisplayName("emailCodeVerification 함수를 테스트합니다.")
    void emailCodeVerificationTest() {
        String code = "test";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(code);
        emailAuthService.emailCodeVerification("test@test.com", code);
    }
}
