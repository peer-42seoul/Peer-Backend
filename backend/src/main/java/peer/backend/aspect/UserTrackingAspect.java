package peer.backend.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import peer.backend.entity.user.User;
import peer.backend.mongo.entity.UserTracking;
import peer.backend.mongo.repository.UserTrackingRepository;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserTrackingAspect {

    private final UserTrackingRepository userTrackingRepository;

    @Pointcut("@annotation(peer.backend.annotation.tracking.UserRegistrationTracking)")
    public void userRegistration() {
    }

    @AfterReturning(pointcut = "peer.backend.aspect.UserTrackingAspect.userRegistration()", returning = "user")
    public void userRegistrationTracking(User user) {
        UserTracking userTracking = UserTracking.builder()
            .userId(user.getId())
            .userEmail(user.getEmail())
            .registrationDate(user.getCreatedAt())
            .build();
        this.userTrackingRepository.save(userTracking);
    }
}
