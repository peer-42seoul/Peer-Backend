package peer.backend.service.profile;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.response.PersonalInfoResponse;
import peer.backend.entity.user.SocialLogin;
import peer.backend.entity.user.User;
import peer.backend.repository.user.SocialLoginRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.SocialLoginService;


@Service
@RequiredArgsConstructor
public class PersonalInfoService {

    private final UserRepository userRepository;
    private final SocialLoginRepository socialLoginRepository;
    private final SocialLoginService socialLoginService;
    private final BCryptPasswordEncoder encoder;

    @Transactional(readOnly = true)
    public PersonalInfoResponse getPersonalInfo(Authentication auth) {
        User user = User.authenticationToUser(auth);
        List<SocialLogin> socialLoginList = this.socialLoginService.getSocialLoginListByUserId(
            user.getId());
        PersonalInfoResponse info = PersonalInfoResponse.builder()
            .name(user.getName())
            .email(user.getEmail())
            .local(user.getAddress())
            .authenticationFt(null)
            .authenticationGoogle(null)
            .build();
        for (SocialLogin socialLogin : socialLoginList) {
            switch (socialLogin.getProvider().getValue()) {
                case "ft":
                    info.setAuthenticationFt(socialLogin.getIntraId());
                    break;
                case "google":
                    info.setAuthenticationGoogle(socialLogin.getEmail());
                    break;
                case "github":
                    break;
            }
        }
        return info;
    }

    @Transactional
    public void changePassword(User user, String password) {
        user.setPassword(encoder.encode(password));
        this.userRepository.save(user);
    }
}
