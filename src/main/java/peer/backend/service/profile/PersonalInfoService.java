package peer.backend.service.profile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, String> redisTemplate;

    final String CHANGE_PASSWORD_KEY = "chagePasswordKey: ";

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

    public String getChangePasswordCode(Long userId) {
        final int CHANGE_PASSWORD_CODE_EXPIRATION_MINUTE = 5;
        UUID uuid = UUID.randomUUID();

        this.redisTemplate.opsForValue()
            .set(CHANGE_PASSWORD_KEY + userId.toString(),
                uuid.toString(),
                CHANGE_PASSWORD_CODE_EXPIRATION_MINUTE,
                TimeUnit.MINUTES);

        return uuid.toString();
    }

    public boolean checkChangePasswordCode(Long userId, String code) {
        String savedCode = this.redisTemplate.opsForValue()
            .get(CHANGE_PASSWORD_KEY + userId.toString());
        if (Objects.isNull(savedCode)) {
            return false;
        }
        return savedCode.equals(code);
    }
}
