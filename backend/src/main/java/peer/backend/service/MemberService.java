package peer.backend.service;

import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.UserRegistrationTracking;
import peer.backend.dto.security.UserInfo;
import peer.backend.entity.user.SocialLogin;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.UnauthorizedException;
import peer.backend.repository.user.SocialLoginRepository;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserRepository userRepository;
    private final SocialLoginRepository socialLoginRepository;
    private final RedisTemplate<String, SocialLogin> redisTemplate;

    private final BCryptPasswordEncoder encoder;

    @UserRegistrationTracking
    @Transactional
    public User signUp(UserInfo info) {
        Optional<User> checkUser = this.userRepository.findByNickname(info.getNickname());
        if (checkUser.isPresent()) {
            throw new ConflictException("이미 존재하는 닉네임입니다.");
        }
        checkUser = this.userRepository.findByEmail(info.getEmail());
        if (checkUser.isPresent()) {
            throw new ConflictException("이미 존재하는 이메일입니다.");
        }
        User user = info.convertUser();
        User savedUser = this.userRepository.save(user);
        String socialEmail = info.getSocialEmail();
        if (socialEmail != null) {
            SocialLogin socialLogin = this.redisTemplate.opsForValue().get(socialEmail);
            if (socialLogin != null) {
                socialLogin.setUser(savedUser);
                this.socialLoginRepository.save(socialLogin);
                this.redisTemplate.delete(socialEmail);
            } else {
                throw new ConflictException("잘못된 소셜 로그인 이메일입니다!");
            }
        }
        return savedUser;
    }

    public boolean verificationPassword(String input, String password) {
        if (!encoder.matches(input, password)) {
            return false;
        }
        return true;
    }

    public void deleteUser(User user) {
        this.userRepository.delete(user);
    }

    public boolean emailDuplicationCheck(String email) {
        User user = this.userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return false;
        }
        SocialLogin socialLogin = this.socialLoginRepository.findByEmail(email).orElse(null);
        if (socialLogin == null) {
            return false;
        }
        return true;
    }
}
