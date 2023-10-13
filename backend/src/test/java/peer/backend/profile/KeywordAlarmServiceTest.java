package peer.backend.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.profile.KeywordAlarmService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test KeywordAlarmService")
public class KeywordAlarmServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private KeywordAlarmService keywordAlarmService;

    String name;
    String keyword;
    User user;
    @BeforeEach
    void beforeEach() {
        name = "test name";
        keyword = "keyword 1^&%keyword 2^&%keyword 3";
        user = User.builder()
                .email("test@email.com")
                .name(name)
                .nickname("test nickname")
                .isAlarm(false)
                .address("test address")
                .keywordAlarm(keyword)
                .build();
    }

    @Test
    @DisplayName("키워드 알람 추가 테스트")
    public void addKeywordTest() {
        when(userRepository.findByName(anyString())).thenReturn(Optional.of(user));
        String newKeyword = "newKeyword";
        keywordAlarmService.addKeyword(name, newKeyword);
        assertThat(user.getKeywordAlarm()).isEqualTo(String.format("%s^&%%%s", keyword, newKeyword));
        String excepKeyword = "keyword 1";
        assertThrows(BadRequestException.class, () -> {
                keywordAlarmService.addKeyword(name, excepKeyword);
            }
        );
    }
}
