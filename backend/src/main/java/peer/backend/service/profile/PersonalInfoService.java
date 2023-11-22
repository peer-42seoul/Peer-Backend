package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.request.PasswordRequest;
import peer.backend.dto.profile.response.PersonalInfoResponse;
import peer.backend.entity.user.SocialLogin;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.user.SocialLoginRepository;
import peer.backend.repository.user.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PersonalInfoService {
    private final UserRepository userRepository;
    private final SocialLoginRepository socialLoginRepository;

    @Transactional(readOnly = true)
    public PersonalInfoResponse getPersonalInfo(Authentication auth) {
        User user = User.authenticationToUser(auth);
        List<SocialLogin> socialLoginList = socialLoginRepository.findAllByUserId(user.getId());
        PersonalInfoResponse info = PersonalInfoResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .local(user.getAddress())
                .authenticationFt(null)
                .authenticationGoogle(null)
                .build();
        for (SocialLogin socialLogin : socialLoginList) {
            switch (socialLogin.getProvider().getValue()) {
                case "ft" :
                    info.setAuthenticationFt("ft");
                    break;
                case "google" :
                    info.setAuthenticationGoogle("google");
                    break;
                case "github" :
                    break;
            }
        }
        return info;
    }

    @Transactional
    public void changePassword(Authentication auth, PasswordRequest passwords) {
        User user = User.authenticationToUser(auth);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(passwords.getPresentPassword(), user.getPassword())) {
            throw new ForbiddenException("현재 비밀번호가 올바르지 않습니다.");
        }
        if (!passwords.getNewPassword().equals(passwords.getConfirmPassword())) {
            throw new BadRequestException("변경할 비밀번호와 일치하지 않습니다.");
        }
        user.setPassword(encoder.encode(passwords.getNewPassword()));
        userRepository.save(user);
    }
}
