package peer.backend.service.profile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import peer.backend.dto.profile.PersonalInfoResponse;
import peer.backend.entity.user.User;
import peer.backend.repository.user.UserRepository;

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
    String email;
    User user;
    @BeforeEach
    void beforeEach() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        email = "test@email.com";
        user = User.builder()
                .id(1L)
                .password(encoder.encode("test password"))
                .email(email)
                .nickname("test nickname")
                .isAlarm(false)
                .address("test address")
                .imageUrl("tes image URL")
                .build();
    }

    @Test
    @DisplayName("개인 정보 조회 테스트")
    public void getPersonalInfoTest() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        PersonalInfoResponse info = personalInfoService.getPersonalInfo(email);
        assertThat(info.getEmail()).isEqualTo(user.getEmail());
        assertThat(info.getName()).isEqualTo(user.getName());
        assertThat(info.getLocal()).isEqualTo(user.getAddress());
        assertThat(info.getAuthentication()).isEqualTo(user.getCompany());
    }
}
