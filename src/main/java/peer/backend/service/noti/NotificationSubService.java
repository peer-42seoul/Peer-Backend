package peer.backend.service.noti;

import com.mongodb.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.noti.enums.*;
import peer.backend.entity.message.MessagePiece;
import peer.backend.entity.noti.Notification;
import peer.backend.entity.noti.NotificationTarget;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.user.User;
import peer.backend.repository.noti.NotificationRepository;
import peer.backend.repository.noti.NotificationSubscriptionKeysRepsitory;
import peer.backend.repository.noti.NotificationTargetRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.*;

// 다른 서비스에서 사용해야 하는 서비스, 메시지를 생성, 삭제 등의 역할을 함
// 그 외에 추가적인 일정 관리나 삭제 여부, 작업 여부 등을 위한 용도
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSubService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;
    private final NotificationSubscriptionKeysRepsitory notificationSubscriptionKeysRepsitory;

    @PersistenceContext
    private final EntityManager entityManager;

    //TODO : 어떻게 할 건지 정하자
    private static final String SYSTEM_DEFAULT = "logo_url";

    private List<?> executeNativeSQLQueryForUser(String sql, Map<String, Long> mapping) {
        Query query = entityManager.createNativeQuery(sql, MessagePiece.class);
        mapping.forEach(query::setParameter);
        return query.getResultList();
    }

    private List<NotificationTarget> makeNorificationTarget(List<User> target, Notification event){
        List<NotificationTarget> result = new ArrayList<>();
        target.forEach(user -> {
            NotificationTarget element = NotificationTarget.builder()
                    .notificationId(event.getId())
                    .specificEvent(event)
                    .build();
                element.setAlarmOptions(user);
                result.add(element);
        });
        return result;
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
            result = this.makeNorificationTarget(users, event);
        }

        return result;
    }

    private List<NotificationTarget> makeUserTargets(Notification event, List<Long> userIds)
            throws NullPointerException{
        List<NotificationTarget> result = new ArrayList<>();
        List<User> targetUsers = userRepository.findByIdIn(userIds);
        if (targetUsers.isEmpty()) {
            throw new NullPointerException("대상이 존재하지 않습니다.");
        } else {
            result = this.makeNorificationTarget(targetUsers, event);
        }

        return result;
    }

    private String getImgUrlWithTeamId(Long teamId) {
        Team target = this.teamRepository.findById(teamId).orElseThrow(() -> new NoSuchElementException("알림을 위한 팀 탐색에 실패하였습니다."));
        return target.getTeamLogoPath();
    }

    private String getImgUrlWithUserId(Long userId) {
        User target = this.userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("알림을 위한 유저 탐색에 실패하였습니다."));
        return target.getImageUrl();
    }

    private String makeImageUrlForNotification(NotificationType type, List<Long> targets){
        String imgUrl = null;
        switch (type) {
            case SYSTEM -> {
                imgUrl = SYSTEM_DEFAULT;
            }
            case TEAM -> {
                if (targets.size() > 1)
                    imgUrl = SYSTEM_DEFAULT;
                else if (targets.size() == 1)
                    imgUrl = getImgUrlWithTeamId(targets.get(0));
            }
            case MESSAGE -> {
                if (targets.size() > 1)
                    imgUrl = SYSTEM_DEFAULT;
                else if (targets.size() == 1)
                    imgUrl = getImgUrlWithUserId(targets.get(0));

            }
        }
        return imgUrl;
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



    /**
     * 표준 방식으로 알람을 생성하는 역할을 하는 메소드.
     * @param targets Long List 를 통해 필요한 대상을 지정한다. User 의 Id, Team 의 Id를 입력 가능합니다.
     * @param notiTarget targets 의 타입을 지정하는 역할을 합니다. TEAM, USER로 나뉩니다.
     * @param title 보낼 알림의 메시지의 타이틀을 나타냅니다. null로 온다면 Peer 가 기본으로 들어갑니다.
     * @param body 보낼 알림의 메시지의 타이틀을 나타냅니다. null 로 와선 안됩니다.
     * @param url redirection을 진행할 URL을 입력합니다.
     * @param type 해당 알림이 어떤 카테고리에 포함되는지를 구분짓습니다.
     * @param priority 해당 알림의 우선순위를 확인한다. 이에따라 강력한 것이면 바로 push 알림을 보낸다.
     * @param time 알림을 전송할 시간을 지정해 줄 수 있다. null 인 경우 바로 보내는 것으로 취급한다.
     * @return
     */
    @Transactional
    public Notification makeAlarm(List<Long> targets,
                                  NotificationTargetType notiTarget,
                                  String title,
                                  String body,
                                  String url,
                                  NotificationType type,
                                  NotificationPriority priority,
                                  LocalDateTime time) {
        // 대표 img URL 지정
        String imgUrl = this.makeImageUrlForNotification(type, targets);

        // Event 생성
        Notification event = Notification.builder()
                .imageUrl(imgUrl)
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

        // NotificationTarget 생성
        List<NotificationTarget> convertedTargets;
        if (notiTarget.equals(NotificationTargetType.TEAM))
            convertedTargets = this.makeTeamTargets(event, targets);
        else
            convertedTargets = this.makeUserTargets(event, targets);
        notificationTargetRepository.saveAll(convertedTargets);

        // 우선순위에 따라서 NestJS 로 전달
        if(event.getPriority().equals(NotificationPriority.FORCE)) {
            //TODO: pushEventDirectly
        } else if (event.getPriority().equals(NotificationPriority.SCHEDULED)) {
            //TODO: pushEventScheduled
        } else {
            //TODO: pushEventImmediately
        }

        return event;
    }

    /**
     * makeAlarm 과 같이 Alarm을 만드는 메소드. 그러나 해당 메소드는 호출시 현재 전체 인원을 기준으로 생성된다.
     * @param title 보낼 알림의 메시지의 타이틀을 나타냅니다. null로 온다면 Peer 가 기본으로 들어갑니다.
     * @param body 보낼 알림의 메시지의 타이틀을 나타냅니다. null 로 와선 안됩니다.
     * @param url redirection을 진행할 URL을 입력합니다.
     * @param type 해당 알림이 어떤 카테고리에 포함되는지를 구분짓습니다.
     * @param priority 해당 알림의 우선순위를 확인한다. 이에따라 강력한 것이면 바로 push 알림을 보낸다.
     * @param time 알림을 전송할 시간을 지정해 줄 수 있다. null 인 경우 바로 보내는 것으로 취급한다.
     * @return
     */
    @Transactional
    public Notification makeAllAlarm(String title,
                                  String body,
                                  String url,
                                  NotificationType type,
                                  NotificationPriority priority,
                                  LocalDateTime time) {
        // 대표 img URL 지정
        // User List 전체 숫자 카운팅
        Long totalUserCount = this.userRepository.countByCreatedAtBefore(LocalDateTime.now());

        //Event 생성
        Notification event = Notification.builder()
                .imageUrl(SYSTEM_DEFAULT)
                .title(title)
                .body(body)
                .linkData(url)
                .targetType(TargetType.ALL)
                .sent(false)
                .priority(priority)
                .messageType(type)
                .scheduledTime(time)
                .totalCount(totalUserCount)
                .deleteCount(0L)
                .build();
        event = notificationRepository.save(event);

        // 우선순위에 따라서 NestJS 로 전달
        if(event.getPriority().equals(NotificationPriority.FORCE)) {
            //TODO: pushEventDirectly
        } else if (event.getPriority().equals(NotificationPriority.SCHEDULED)) {
            //TODO: pushEventScheduled
        } else {
            //TODO: pushEventImmediately
        }
        return event;
    }

    /**
     * 이미 생성된 알람에 대해 수정하는 메소드이다. 필수적으로 eventId를 검색해내야 하며, 나머지 값은 null 이 들어갈 수 있고, null 이 아닌 값에 대해서만 수정이 된다.
     * @param eventId
     * @param targets
     * @param notiTarget
     * @param title
     * @param body
     * @param url
     * @param time
     * @return
     */
    @Transactional
    public Notification updateAlarm(Long eventId,
                                    @Nullable List<Long> targets,
                                    @Nullable NotificationTargetType notiTarget,
                                    @Nullable String title,
                                    @Nullable String body,
                                    @Nullable String url,
                                    @Nullable LocalDateTime time) {
        Notification event = this.notificationRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException("갱신 가능한 알림이 아닙니다."));
        List<NotificationTarget> convertedTargets = new ArrayList<>();

        if (event.getTargetType().equals(TargetType.CERTAIN)) {
            List<NotificationTarget> earlyTargets = event.getTargets();
            this.notificationTargetRepository.deleteAll(earlyTargets);

            if (notiTarget != null && notiTarget.equals(NotificationTargetType.TEAM))
            {
                convertedTargets = this.makeTeamTargets(event, targets);
            } else if (notiTarget != null && notiTarget.equals(NotificationTargetType.USER)) {
                convertedTargets = this.makeUserTargets(event, targets);
            }
            this.notificationTargetRepository.saveAll(convertedTargets);
        }

        if (title != null)
            event.setTitle(title);
        if(body != null)
            event.setBody(body);
        if(url != null)
            event.setLinkData(url);
        if(!event.sent)
            event.setScheduledTime(time);

        return this.notificationRepository.save(event);
    }

    /**
     * 기준이 되는 eventList 를 받는 것을 기본으로 합니다. 이는, 1개일 때도 동일하며, List 형태로 전달받는다.
     * @param eventId
     * @return
     */
    @Transactional
    public boolean deleteAlarm(List<Long> eventId) {
        if (eventId == null)
            return false;
        if(!this.notificationRepository.existsAllByIdIn(eventId, eventId.size()))
            return false;
        entityManager.flush();
        this.notificationRepository.deleteAllByIdInBatch(eventId);
        entityManager.clear();
        return true;
    }

    /**
     * 기존에 생성된 Notification 을 검색해내며 키워드 검색이 가능하다.
     * @param keyword 최소 2자 ~ 최대 10자의 title, body를 무작위로 검색합니다.
     * @return
     */
    public List<Notification> searchAlarm(@Size(min=2, max=10, message = "검색 가능한 키워드 사이즈는 2자에서 10자까지입니다.") String keyword) {
        return this.notificationRepository.findByKeyword(keyword);
    }

    /**
     * 기존에 생성된 Notification 을 검색해내며
     * @param standard 특정 시점으로써 기준으로 리스트를 검색하는 용도.
     * @param quarter 특정 시점을 어떤 기준으로 리스트를 검색할 지를 구분한다.
     * @return
     */
    public List<Notification> searchAlarm(LocalDateTime standard, ScheduledStandard quarter) {

        if (quarter.equals(ScheduledStandard.AFTER))
            return this.notificationRepository.findByCreatedAtAfter(standard);
        else
            return this.notificationRepository.findByCreatedAtBefore(standard);
    }
}
