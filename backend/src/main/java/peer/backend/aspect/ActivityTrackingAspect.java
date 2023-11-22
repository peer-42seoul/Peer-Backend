package peer.backend.aspect;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.team.Team;
import peer.backend.entity.user.SocialLogin;
import peer.backend.entity.user.User;
import peer.backend.mongo.entity.ActivityTracking;
import peer.backend.mongo.entity.enums.ActionType;
import peer.backend.mongo.repository.ActivityTrackingRepository;
import peer.backend.service.UserService;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class ActivityTrackingAspect {

    private final ActivityTrackingRepository activityTrackingRepository;
    private final UserService userService;

    @Pointcut("@annotation(peer.backend.annotation.tracking.UserRegistrationTracking)")
    public void userRegistration() {
    }

    @Pointcut("@annotation(peer.backend.annotation.tracking.RecruitWritingTracking)")
    public void recruitWriting() {
    }

    @Pointcut("@annotation(peer.backend.annotation.tracking.UserWithdrawalTracking)")
    public void userWithdrawal() {
    }

    @Order(0)
    @AfterReturning(pointcut = "peer.backend.aspect.ActivityTrackingAspect.userRegistration()", returning = "user")
    public void userRegistrationTracking(User user) {
        ActivityTracking activityTracking = ActivityTracking.builder()
            .userId(user.getId())
            .intraId(this.getIntraId(user))
            .actionType(ActionType.REGISTRATION)
            .build();

        activityTrackingRepository.save(activityTracking);
    }

    @AfterReturning(pointcut = "peer.backend.aspect.ActivityTrackingAspect.recruitWriting()", returning = "recruit")
    public void recruitWriting(Recruit recruit) throws Throwable {
        User user = this.userService.findByEmail(recruit.getWriter().getEmail());

        Team team = recruit.getTeam();

        ActivityTracking activityTracking = ActivityTracking.builder()
            .userId(user.getId())
            .intraId(this.getIntraId(user))
            .registeredTeamId(team.getId())
            .teamType(team.getType())
            .actionType(ActionType.WRITING)
            .build();

        activityTrackingRepository.save(activityTracking);
    }

    @AfterReturning(pointcut = "userWithdrawal()", returning = "user")
    public void userWithdrawalTracking(User user) {
        ActivityTracking activityTracking = ActivityTracking.builder()
            .userId(user.getId())
            .actionType(ActionType.WITHDRAWAL)
            .build();
        this.activityTrackingRepository.save(activityTracking);
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
