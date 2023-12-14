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
import peer.backend.mongo.entity.ActionTracking;
import peer.backend.mongo.entity.enums.ActionType;
import peer.backend.mongo.repository.ActionTrackingRepository;
import peer.backend.service.UserService;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class ActionTrackingAspect {

    private final ActionTrackingRepository actionTrackingRepository;
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
    @AfterReturning(pointcut = "peer.backend.aspect.ActionTrackingAspect.userRegistration()", returning = "user")
    public void userRegistrationTracking(User user) {
        ActionTracking actionTracking = ActionTracking.builder()
            .userId(user.getId())
            .intraId(this.getIntraId(user))
            .actionType(ActionType.REGISTRATION)
            .build();

        actionTrackingRepository.save(actionTracking);
    }

    @AfterReturning(pointcut = "peer.backend.aspect.ActionTrackingAspect.recruitWriting()", returning = "recruit")
    public void recruitWriting(Recruit recruit) throws Throwable {
        User user = this.userService.findByEmail(recruit.getWriter().getEmail());

        Team team = recruit.getTeam();

        ActionTracking actionTracking = ActionTracking.builder()
            .userId(user.getId())
            .intraId(this.getIntraId(user))
            .registeredTeamId(team.getId())
            .teamType(team.getType())
            .actionType(ActionType.WRITING)
            .build();

        actionTrackingRepository.save(actionTracking);
    }

    @AfterReturning(pointcut = "userWithdrawal()", returning = "user")
    public void userWithdrawalTracking(User user) {
        ActionTracking actionTracking = ActionTracking.builder()
            .userId(user.getId())
            .actionType(ActionType.WITHDRAWAL)
            .build();
        this.actionTrackingRepository.save(actionTracking);
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
