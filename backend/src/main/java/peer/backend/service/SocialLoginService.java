package peer.backend.service;

import java.util.concurrent.TimeUnit;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import peer.backend.entity.user.SocialLogin;
import peer.backend.repository.user.SocialLoginRepository;

@RequiredArgsConstructor
@Service
public class SocialLoginService {

    private final SocialLoginRepository socialLoginRepository;
    private final RedisTemplate<String, SocialLogin> redisTemplate;
    public static final String SOCIAL_REDIS_KEY_PREFIX = "social-email:";

    @Transactional
    public SocialLogin getSocialLogin(String email) {
        return this.socialLoginRepository.findByEmail(email).orElse(null);
    }

    public SocialLogin save(SocialLogin socialLogin) {
        return this.socialLoginRepository.save(socialLogin);
    }

    public void putSocialLoginInRedis(SocialLogin socialLogin) {
        this.redisTemplate.opsForValue()
            .set(SOCIAL_REDIS_KEY_PREFIX + socialLogin.getEmail(), socialLogin, 3,
                TimeUnit.HOURS);
    }

    public void deleteSocialLoginInRedis(String email) {
        this.redisTemplate.delete(SOCIAL_REDIS_KEY_PREFIX + email);
    }

    public SocialLogin getSocialLoginInRedis(String email) {
        return this.redisTemplate.opsForValue().get(SOCIAL_REDIS_KEY_PREFIX + email);
    }
}
