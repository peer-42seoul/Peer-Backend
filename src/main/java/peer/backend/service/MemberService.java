package peer.backend.service;

import java.security.SecureRandom;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.UserRegistrationTracking;
import peer.backend.annotation.tracking.UserWithdrawalTracking;
import peer.backend.dto.security.UserInfo;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.user.SocialLogin;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.SocialLoginRepository;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final UserRepository userRepository;
    private final SocialLoginRepository socialLoginRepository;
    private final SocialLoginService socialLoginService;
    private final RedisTemplate<String, SocialLogin> redisTemplate;
    private final UserService userService;

    private final BCryptPasswordEncoder encoder;

    //TODO: 뺄게 있을까?
    private final String randomSpecialkeys = "!@#$%^&*";

    @Transactional
    @UserRegistrationTracking
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
        User savedUser = this.userService.saveUser(user);
        String socialEmail = info.getSocialEmail();
        if (socialEmail != null) {
            SocialLogin socialLogin = this.socialLoginService.getSocialLoginInRedis(socialEmail);
            if (socialLogin != null) {
                socialLogin.setUser(savedUser);
                this.socialLoginService.save(socialLogin);
                this.socialLoginService.deleteSocialLoginInRedis(socialEmail);
                savedUser.addSocialLogin(socialLogin);
            } else {
                throw new ConflictException("잘못된 소셜 로그인 이메일입니다!");
            }
        }
        return savedUser;
    }

    @Transactional
    public boolean verificationPassword(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    @Transactional
    @UserWithdrawalTracking
    public User deleteUser(User user) {
        User findUser = userService.findByEmail(user.getEmail());
        for (Recruit recruit : findUser.getRecruitList()) {
            recruit.setWriter(null);
            recruit.setWriterId(null);
        }
        this.userRepository.delete(findUser);
        return user;
    }

    @Transactional
    public boolean emailDuplicationCheck(String email) {
        User user = this.userRepository.findByEmail(email).orElse(null);
        if (this.userRepository.existsByEmail(email)) {
            return true;
        }
        return this.socialLoginRepository.existsByEmail(email);
    }

    @Transactional
    public boolean emailExistsCheck(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Transactional
    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("해당 이메일의 유저가 없습니다!"));
    }

    @Transactional
    public void changePassword(User user, String password) {
        user.setPassword(encoder.encode(password));
    }

    public String getRandomPassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String chars_little = "abcdefghijklmnopqrstuvwxyz";
        final String numbers = "0123456789";

        SecureRandom rm = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            //무작위로 문자열의 인덱스 반환
            int index = rm.nextInt(chars.length());
            //index의 위치한 랜덤값
            if (i % 2 != 0)
                sb.append(chars.charAt(index));
            else
                sb.append(chars_little.charAt(index));
        }

        int specialKey1 = Math.abs(rm.nextInt() % randomSpecialkeys.length());
        int specialKey2 = Math.abs(rm.nextInt() % randomSpecialkeys.length());
        int specialKey3 = Math.abs(rm.nextInt() % numbers.length());
        int specialKey4 = Math.abs(rm.nextInt() % numbers.length());

        sb.append(randomSpecialkeys.charAt(specialKey1));
        sb.append(numbers.charAt(specialKey3));
        sb.append(randomSpecialkeys.charAt(specialKey2));
        sb.append(numbers.charAt(specialKey4));

        return sb.toString();
    }
}
