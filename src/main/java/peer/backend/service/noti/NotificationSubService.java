package peer.backend.service.noti;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import peer.backend.dto.noti.enums.NotificationPriority;
import peer.backend.dto.noti.enums.NotificationTargetType;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.dto.noti.enums.TargetType;
import peer.backend.entity.message.MessagePiece;
import peer.backend.entity.noti.Notification;
import peer.backend.entity.noti.NotificationTarget;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.user.User;
import peer.backend.repository.noti.NotificationRepository;
import peer.backend.repository.noti.NotificationSubscriptionKeysRepsitory;
import peer.backend.repository.noti.NotificationTargetRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.*;

// 다른 서비스에서 사용해야 하는 서비스, 메시지를 생성, 삭제 등의 역할을 함
// 그 외에 추가적인 일정 관리나 삭제 여부, 작업 여부 등을 위한 용도
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSubService {
    private final UserRepository userRepository;
    private final TeamUserRepository teamUserRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;
    private final NotificationSubscriptionKeysRepsitory notificationSubscriptionKeysRepsitory;

    private final EntityManager entityManager;

    private List<?> executeNativeSQLQueryForUser(String sql, Map<String, Long> mapping) {
        Query query = entityManager.createNativeQuery(sql, MessagePiece.class);
        mapping.forEach(query::setParameter);
        return query.getResultList();
    }

    private List<NotificationTarget> makeTeamTargets(Notification event, List<Long> teamIds)
            throws NullPointerException{
        List<NotificationTarget> result = new ArrayList<>();

        Query query = entityManager.createQuery("SELECT u FROM User u JOIN TeamUser tu ON tu.userId = u.id " +
                "JOIN Team t ON t.id = tu.teamId " +
                "WHERE t.id IN :teamIds");
        query.setParameter("teamIds", teamIds);
        List<User> users = query.getResultList();


        List<TeamUser> targetTeamUsers = teamUserRepository.findByIdIn(teamIds);
        if (targetTeamUsers.isEmpty()) {
            throw new NullPointerException("대상이 존재하지 않습니다.");
        } else {
            users.forEach(user -> {
                NotificationTarget target = NotificationTarget.builder()
                        .notificationId(event.getId())
                        .specificEvent(event)
                        .build();
                target.setAlarmOptions(user);
                result.add(target);
            });
        }

        return result;
    }

    private List<NotificationTarget> makeUserTargets(Notification event, List<Long> userIds)
            throws NullPointerException{
        List<NotificationTarget> result = new ArrayList<>();
        List<User> targetUsers = userRepository.findbyIdIn(userIds);
        if (targetUsers.isEmpty()) {
            throw new NullPointerException("대상이 존재하지 않습니다.");
        } else {
            targetUsers.forEach(user -> {
                NotificationTarget target = NotificationTarget.builder()
                        .notificationId(event.getId())
                        .specificEvent(event)
                        .build();
                target.setAlarmOptions(user);
                result.add(target);
            });
        }

        return result;
    }
    public Notification makeAlarm(List<Long> targets,
                                  NotificationTargetType notiTarget,
                                  String title,
                                  String body,
                                  String url,
                                  NotificationType type,
                                  NotificationPriority priority,
                                  LocalDateTime time) {

        // 순서
        // Notification
        // Notification Target(event 등록)
        // 울리기

        Notification event = Notification.builder()
                .title(title)
                .body(body)
                .linkData(url)
                .targetType(TargetType.CERTAIN)
                .sent(false)
                .priority(priority)
                .messageType(type)
                .scheduledTime(time)
                .totalCount((long) targets.size())
                .deleteCount(0L)
                .build();

        event = notificationRepository.save(event);
        List<NotificationTarget> convertedTargets;

        if (notiTarget.equals(NotificationTargetType.TEAM))
            convertedTargets = this.makeTeamTargets(event, targets);
        else
            convertedTargets = this.makeUserTargets(event, targets);
        notificationTargetRepository.saveAll(convertedTargets);

        // TODO: makeRing


        return event;
    }

    /**
     * User 의 목록을 통해 알람으로 전달하기 위한 `List\<Long\>` 을 쉽게 반환받는다.
     * @param users
     * @return
     */
    public static List<Long> makeLongListWithUserList(List<User> users){
        List<Long> result = new ArrayList<>();
        if (users.isEmpty()) {
            return null;
        }
        users.forEach(user -> {
            result.add(user.getId());
        });
        return result;
    }

    /**
     * TeamUser 의 목록을 통해 알림으로 전달하기 위한 `List\<Long\>` 을 쉽게 반환받는다.
     * @param users
     * @return
     */
    public static List<Long> makeLongListWithTeamList(List<TeamUser> users){
        List<Long> result = new ArrayList<>();
        if (users.isEmpty())
            return null;
        users.forEach(user -> {
            result.add(user.getUserId());
        });
        return result;
    }

    /**
     * Team 객체를 통해 알림으로 전달하기 위한 `List\<Long\>` 을 쉽게 반환받는다.
     * @param teams
     * @return
     */
    public static List<Long> makeLongListWithTeam(List<Team> teams) {
        List<Long> result = new ArrayList<>();
        if (teams.isEmpty())
            return null;
        teams.forEach(team -> {
            List<TeamUser> values = team.getTeamUsers();
            List<Long> longParts = NotificationSubService.makeLongListWithTeamList(values);
            result.addAll(Objects.requireNonNull(longParts));
        });
        return result;
    }


    public Notification makeAllAlarm(String title,
                                  String body,
                                  String url,
                                  NotificationType type,
                                  NotificationPriority priority,
                                  LocalDateTime time) {
        return new Notification();
    }
}
