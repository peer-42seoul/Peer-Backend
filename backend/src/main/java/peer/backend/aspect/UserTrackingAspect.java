package peer.backend.aspect;

import java.time.LocalDate;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import peer.backend.entity.user.SocialLogin;
import peer.backend.entity.user.User;
import peer.backend.mongo.entity.UserTracking;
import peer.backend.mongo.entity.enums.UserTrackingStatus;
import peer.backend.mongo.repository.UserTrackingRepository;
import peer.backend.oauth.enums.SocialLoginProvider;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserTrackingAspect {

    private final UserTrackingRepository userTrackingRepository;
    @Pointcut("@annotation(peer.backend.annotation.tracking.UserRegistrationTracking)")
    public void userRegistration() {
    }

    @Pointcut("@annotation(peer.backend.annotation.tracking.UserFtLinkTracking)")
    public void userFtLink() {
    }

    @Pointcut("@annotation(peer.backend.annotation.tracking.UserWithdrawalTracking)")
    public void userWithdrawal() {
    }

    @AfterReturning(pointcut = "peer.backend.aspect.UserTrackingAspect.userRegistration()", returning = "user")
    public void userRegistrationTracking(User user) {
        UserTracking userTracking = UserTracking.builder()
            ._id(user.getId())
            .userId(user.getId())
            .userEmail(user.getEmail())
            .registrationDate(user.getCreatedAt().toLocalDate())
            .intraId(null)
            .status(UserTrackingStatus.NORMAL)
            .build();
        this.userTrackingRepository.save(userTracking);
    }

    @AfterReturning(pointcut = "peer.backend.aspect.UserTrackingAspect.userFtLink()", returning = "socialLogin")
    public void userOAuthTacking(SocialLogin socialLogin) {
        if (socialLogin.getProvider() == SocialLoginProvider.FT) {
            UserTracking userTracking = this.userTrackingRepository.findByUserId(
                socialLogin.getUser().getId());
            userTracking.setIntraId(socialLogin.getIntraId());
            userTracking.setFtOAuthRegistered(true);
            this.userTrackingRepository.save(userTracking);
        }
    }

    @Transactional
    @AfterReturning(pointcut = "peer.backend.aspect.UserTrackingAspect.userWithdrawal()", returning = "user")
    public void userWithdrawalTracking(User user) {
        UserTracking userTracking = this.userTrackingRepository.findByUserId(user.getId());
        userTracking.setUnRegistrationDate(LocalDate.now());
        userTracking.setStatus(UserTrackingStatus.WITHDRAWAL);
        this.userTrackingRepository.save(userTracking);
    }
}
