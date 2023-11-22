package peer.backend.profile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import peer.backend.dto.profile.request.PasswordRequest;
import peer.backend.dto.profile.response.PersonalInfoResponse;
import peer.backend.entity.user.SocialLogin;
import peer.backend.entity.user.User;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.oauth.enums.SocialLoginProvider;
import peer.backend.repository.user.SocialLoginRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.profile.PersonalInfoService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test PersonalInfoService")
public class PersonalInfoServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    SocialLoginRepository socialLoginRepository;
    @InjectMocks
    PersonalInfoService personalInfoService;
    String name;
    User user;
    String password;
    PasswordRequest newPassword;
    Authentication auth;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    List<SocialLogin> socialLogins;
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
        PrincipalDetails details = new PrincipalDetails(user);
        auth = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
        socialLogins = new ArrayList<>();
        SocialLogin socialLogin1 = SocialLogin.builder()
                .user(user)
                .intraId("intraId")
                .provider(SocialLoginProvider.FT)
                .build();
        SocialLogin socialLogin2 = SocialLogin.builder()
                .user(user)
                .email("gmail")
                .provider(SocialLoginProvider.GOOGLE)
                .build();
        socialLogins.add(socialLogin1);
        socialLogins.add(socialLogin2);
    }

    @Test
    @DisplayName("개인 정보 조회 테스트")
    public void getPersonalInfoTest() {
        when(socialLoginRepository.findAllByUserId(anyLong())).thenReturn(socialLogins);
        PersonalInfoResponse info = personalInfoService.getPersonalInfo(auth);
        assertThat(info.getEmail()).isEqualTo(user.getEmail());
        assertThat(info.getName()).isEqualTo(user.getName());
        assertThat(info.getLocal()).isEqualTo(user.getAddress());
        assertThat(info.getAuthenticationFt()).isEqualTo("intraId");
        assertThat(info.getAuthenticationGoogle()).isEqualTo("gmail");
    }

    @Test
    @DisplayName("비밀 번호 변경")
    void changePasswordTest() {
        personalInfoService.changePassword(auth, newPassword);
        assertThat(encoder.matches("new password", user.getPassword())).isTrue();
    }
}
