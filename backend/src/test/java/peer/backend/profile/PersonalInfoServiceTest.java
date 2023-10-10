package peer.backend.profile;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import peer.backend.dto.profile.information.PersonalInfoResponse;
import peer.backend.entity.user.User;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.profile.PersonalInfoService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test PersonalInfoService")
public class PersonalInfoServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    PersonalInfoService personalInfoService;
    User user;
    String email;
    String password;
    @BeforeEach
    void beforeEach() {
        email = "test@email.com";
        password = "test password";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user = User.builder()
                .id(1L)
                .password(encoder.encode(password))
                .email(email)
                .nickname("test1234")
                .isAlarm(false)
                .address("test address")
                .build();
    }

    @Test
    @DisplayName("개인정보 조회 테스트")
    void getPersonalInfoTest() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        PersonalInfoResponse info = personalInfoService.getPersonalInfo(email);
        assertThat(info.getEmail()).isEqualTo(email);
    }
}
