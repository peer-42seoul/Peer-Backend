package peer.backend.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import peer.backend.dto.profile.KeywordResponse;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.profile.KeywordAlarmService;

import java.util.Collection;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test KeywordAlarmService")
public class KeywordAlarmServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private KeywordAlarmService keywordAlarmService;

    String name;
    User user;
    Authentication auth;
    @BeforeEach
    void beforeEach() {
        name = "test name";
        user = User.builder()
                .email("test@email.com")
                .name(name)
                .nickname("test nickname")
                .teamAlarm(true)
                .nightAlarm(true)
                .messageAlarm(true)
                .keywordRecommendAlarm(true)
                .address("test address")
                .build();
        PrincipalDetails details = new PrincipalDetails(user);
        auth = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
    }

    @Test
    @DisplayName("키워드 알람 추가 테스트")
    public void addKeywordTest() {
        String newKeyword1 = "test1";
        keywordAlarmService.addKeyword(auth, newKeyword1);
        assertThat(user.getKeywordAlarm()).isEqualTo(newKeyword1);
        String newKeyword2 = "test2";
        keywordAlarmService.addKeyword(auth, newKeyword2);
        assertThat(user.getKeywordAlarm()).isEqualTo(String.format("%s^&%%%s", newKeyword1, newKeyword2));
        String excepKeyword = "test1";
        assertThrows(BadRequestException.class, () -> {
                keywordAlarmService.addKeyword(auth, excepKeyword);
            }
        );
    }

    @Test
    @DisplayName("키워드 알람 조회 테스트")
    public void getKeywordTest() {
        KeywordResponse ret = keywordAlarmService.getKeyword(auth);
        assertThat(ret.getKeyword()).isNull();
        String newKeyword = "test1";
        keywordAlarmService.addKeyword(auth, newKeyword);
        ret = keywordAlarmService.getKeyword(auth);
        assertThat(ret.getKeyword()).isEqualTo(newKeyword);
    }

    @Test
    @DisplayName("키워드 알람 삭제 테스트")
    public void deleteKeywordTest() {
        user.setKeywordAlarm("test1^&%test2^&%test3^&%test4");
        keywordAlarmService.deleteKeyword(auth, "test2");
        assertThat(user.getKeywordAlarm()).isEqualTo("test1^&%test3^&%test4");
        user.setKeywordAlarm("");
        assertThrows(BadRequestException.class, () -> {
                    keywordAlarmService.deleteKeyword(auth, "test1");
                }
        );
    }

    @Test
    @DisplayName("키워드 알람 전부 삭제 테스트")
    public void deleteAllTest() {
        user.setKeywordAlarm("test1^&%test2^&%test3^&%test4");
        keywordAlarmService.deleteAll(auth);
        assertThat(user.getKeywordAlarm()).isNull();
    }
}
