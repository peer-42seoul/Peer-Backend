package peer.backend.aspect;


import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import peer.backend.entity.user.SocialLogin;
import peer.backend.entity.user.User;
import peer.backend.mongo.SequenceGeneratorService;
import peer.backend.mongo.entity.ActivityTracking;
import peer.backend.mongo.entity.enums.ActionType;
import peer.backend.mongo.repository.ActivityTrackingRepository;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class ActivityTrackingAspect {

    private final ActivityTrackingRepository activityTrackingRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Pointcut("@annotation(peer.backend.annotation.tracking.UserRegistrationTracking)")
    public void userRegistration() {
    }

    @AfterReturning(pointcut = "peer.backend.aspect.UserTrackingAspect.userRegistration()", returning = "user")
    public void userRegistrationTracking(User user) {
        ActivityTracking activityTracking = ActivityTracking.builder()
            .userId(user.getId())
            .intraId(this.getIntraId(user))
            .actionType(ActionType.REGISTRATION)
            .actDate(LocalDate.now())
            .build();

        activityTrackingRepository.save(activityTracking);
    }

    private String getIntraId(User user) {
        List<SocialLogin> socialLoginList = user.getSocialLogins();

        if (socialLoginList != null) {
            for (SocialLogin socialLogin : socialLoginList) {
                if (socialLogin.getIntraId() != null) {
                    return socialLogin.getIntraId();
                }
            }
        }

        return null;
    }
}
