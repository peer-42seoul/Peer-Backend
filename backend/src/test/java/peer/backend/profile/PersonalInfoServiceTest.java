package peer.backend.profile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import peer.backend.dto.profile.request.PasswordRequest;
import peer.backend.dto.profile.response.PersonalInfoResponse;
import peer.backend.entity.user.User;
import peer.backend.oauth.PrincipalDetails;
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
    String name;
    User user;
    String password;
    PasswordRequest newPassword;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    PrincipalDetails principalDetails;
    @BeforeEach
    void beforeEach() {
        name = "test name";
        password = "test password";
        user = User.builder()
                .id(1L)
                .email("test@email.com")
                .password(encoder.encode("test password"))
                .name(name)
                .nickname("test nickname")
                .isAlarm(false)
                .address("test address")
                .imageUrl("tes image URL")
                .build();
        newPassword = new PasswordRequest(
                password, "new password", "new password"
        );
        principalDetails = new PrincipalDetails(user);
    }

    @Test
    @DisplayName("개인 정보 조회 테스트")
    public void getPersonalInfoTest() {
        PersonalInfoResponse info = personalInfoService.getPersonalInfo(principalDetails);
        assertThat(info.getEmail()).isEqualTo(user.getEmail());
        assertThat(info.getName()).isEqualTo(user.getName());
        assertThat(info.getLocal()).isEqualTo(user.getAddress());
        assertThat(info.getAuthentication()).isEqualTo(user.getCompany());
    }

    @Test
    @DisplayName("비밀 번호 변경")
    void changePasswordTest() {
        personalInfoService.changePassword(principalDetails, newPassword);
        assertThat(encoder.matches("new password", user.getPassword())).isTrue();
    }
}
