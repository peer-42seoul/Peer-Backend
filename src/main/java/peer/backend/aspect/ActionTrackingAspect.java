package peer.backend.aspect;


import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import peer.backend.entity.action.Wallet;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.team.Team;
import peer.backend.entity.user.SocialLogin;
import peer.backend.entity.user.User;
import peer.backend.mongo.entity.ActionTracking;
import peer.backend.mongo.entity.enums.ActionTypeEnum;
import peer.backend.mongo.repository.ActionTrackingRepository;
import peer.backend.service.UserService;
import peer.backend.service.action.ActionTypeService;
import peer.backend.service.action.WalletService;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class ActionTrackingAspect {

    private final ActionTrackingRepository actionTrackingRepository;
    private final UserService userService;
    private final ActionTypeService actionTypeService;
    private final WalletService walletService;

    @Pointcut("@annotation(peer.backend.annotation.tracking.UserRegistrationTracking)")
    public void userRegistration() {
    }

    @Pointcut("@annotation(peer.backend.annotation.tracking.RecruitWritingTracking)")
    public void recruitWriting() {
    }

    @Pointcut("@annotation(peer.backend.annotation.tracking.UserWithdrawalTracking)")
    public void userWithdrawal() {
    }

    @Pointcut("@annotation(peer.backend.annotation.tracking.PostCreateTracking)")
    public void postCreate() {
    }

    @Order(0)
    @AfterReturning(pointcut = "peer.backend.aspect.ActionTrackingAspect.userRegistration()", returning = "user")
    public void userRegistrationTracking(User user) {
        ActionTypeEnum actionTypeEnum = ActionTypeEnum.REGISTRATION;
        Wallet wallet = this.walletService.getWalletToActionTypeCode(actionTypeEnum.getCode());

        ActionTracking actionTracking = ActionTracking.builder()
            .userId(user.getId())
            .intraId(this.getIntraId(user))
            .actionTypeEnum(actionTypeEnum)
            .wallet(wallet.getValue())
            .build();

        actionTrackingRepository.save(actionTracking);
    }

    @AfterReturning(pointcut = "peer.backend.aspect.ActionTrackingAspect.recruitWriting()", returning = "recruit")
    public void recruitWriting(Recruit recruit) throws Throwable {
        ActionTypeEnum actionTypeEnum = ActionTypeEnum.RECRUIT_WRITING;
        Wallet wallet = this.walletService.getWalletToActionTypeCode(actionTypeEnum.getCode());
        User user = this.userService.findByEmail(recruit.getWriter().getEmail());

        Team team = recruit.getTeam();

        ActionTracking actionTracking = ActionTracking.builder()
            .userId(user.getId())
            .intraId(this.getIntraId(user))
            .registeredTeamId(team.getId())
            .teamType(team.getType())
            .actionTypeEnum(actionTypeEnum)
            .wallet(wallet.getValue())
            .build();

        actionTrackingRepository.save(actionTracking);
    }

    @AfterReturning(pointcut = "userWithdrawal()", returning = "user")
    public void userWithdrawalTracking(User user) {
        ActionTypeEnum actionTypeEnum = ActionTypeEnum.WITHDRAWAL;
        Wallet wallet = this.walletService.getWalletToActionTypeCode(actionTypeEnum.getCode());

        ActionTracking actionTracking = ActionTracking.builder()
            .userId(user.getId())
            .actionTypeEnum(actionTypeEnum)
            .wallet(wallet.getValue())
            .build();
        this.actionTrackingRepository.save(actionTracking);
    }

    @AfterReturning(pointcut = "postCreate()", returning = "post")
    public void postCreate(Post post) {
        ActionTypeEnum actionTypeEnum = ActionTypeEnum.TEAM_POST_WRITING;
        Wallet wallet = this.walletService.getWalletToActionTypeCode(actionTypeEnum.getCode());

        ActionTracking actionTracking = ActionTracking.builder()
            .userId(post.getUser().getId())
            .actionTypeEnum(actionTypeEnum)
            .wallet(wallet.getValue())
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
