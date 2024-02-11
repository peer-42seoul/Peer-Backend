package peer.backend.service.noti;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import peer.backend.dto.noti.enums.NotificationPriority;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.entity.noti.Notification;
import peer.backend.entity.noti.NotificationTarget;
import peer.backend.entity.user.User;
import peer.backend.repository.noti.NotificationRepository;
import peer.backend.repository.noti.NotificationSubscriptionKeysRepsitory;
import peer.backend.repository.noti.NotificationTargetRepository;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// 다른 서비스에서 사용해야 하는 서비스, 메시지를 생성, 삭제 등의 역할을 함
// 그 외에 추가적인 일정 관리나 삭제 여부, 작업 여부 등을 위한 용도
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCreationService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;
    private final NotificationSubscriptionKeysRepsitory notificationSubscriptionKeysRepsitory;

    @PersistenceContext
    private final EntityManager entityManager;

    //TODO : 어떻게 할 건지 정하자
    @Value("${nhn.default.icon}")
    private String DEFAULTICON;

    private final String DEFAULT_DELIMITTER = "###";

    /**
     * teamId를 통해 사용자의 Id 리스트를 확보한다.
     * @param teamId 팀의 ID값
     * @return User Id 값의 목록
     */
    private List<Long> makeUserListFromTeamId(Long teamId) {
        return this.teamUserRepository.findUserIdsIn(teamId);
    }

    /**
     * teamId의 List를 통해 모든 UserList 를 확보한다.
     * @param teamIds 팀의 ID 값의 목록
     * @return User Id 값의 목록
     */
    private List<Long> makeUserListFromTeamIds(List<Long> teamIds) {
        return this.teamUserRepository.findAllUserIdsIn(teamIds);
    }

    /**
     * 생성된 이벤트에 대한 User 대상을 생성한다.
     * @param event 타겟이 되는 알림
     * @param type 어떤 타입의 알림인지를 기록하는 용도
     * @param userId 알림의 대상
     * @return 생성된 알림에 대한 대상
     */
    private NotificationTarget makeOneTargetForEvent(Notification event, NotificationType type, Long userId) {
        return NotificationTarget.builder()
                .notificationId(event.getId())
                .userList(userId + DEFAULT_DELIMITTER)
                .messageType(type)
                .columnIndex(userId / 100)
                .specificEvent(event)
                .build();
    }

    /**
     * 생성된 이벤트에 대한 User 대상을 생성한다.
     * @param event 타겟이 되는 알림
     * @param type 어떤 타입의 알림인지를 기록하는 용도
     * @param userIds 알림의 대상 목록
     * @return 생성된 알림에 대한 대상 목록
     */
    private List<NotificationTarget> makeTargetsForEvent(Notification event, NotificationType type, List<Long> userIds) {
        List<NotificationTarget> results = new ArrayList<>();
        List<Long> index = new ArrayList<>();

        if (userIds == null)
            throw new NullPointerException("잘못된 접근을 하셨습니다.");

        userIds.forEach(userId -> {
            Long indexPiece = userId / 100;
            int position = index.indexOf(indexPiece);
            if (position == -1) {
                index.add(indexPiece);
                NotificationTarget newData = NotificationTarget.builder()
                        .notificationId(event.getId())
                        .columnIndex(indexPiece)
                        .messageType(type)
                        .specificEvent(event)
                        .userList(userId + DEFAULT_DELIMITTER)
                        .build();
                results.add(newData);
            }
            else {
                results.stream()
                        .filter(notificationTarget -> indexPiece.equals(notificationTarget.getColumnIndex()))
                        .findFirst()
                        .ifPresent(eventTarget -> eventTarget.appendUserId(userId));
            }
        });
        return results;
    }

    // 기본적으로 팀 별로 이미지를 보낼 때가 존재함.
    // 팀 전체 리스트에게 보내는 방법은 애매
    // 1인 알림
    // 다인 알림
    // 1 팀 알림
    // 다 팀 알림

    /**
     * 한 사람을 위한 알림
     * @param title 알림의 타이틀을 지정한다. 지정된 값이 없다면 Default로 peer 가 들어간다.
     * @param body 알림의 메시지 값을 지정한다. 지정된 값이 반드시 필요하다.
     * @param link 알림에서 리다이렉션 들어갈 링크를 지정한다. 반드시 필요하다.
     * @param priority 우선순위를 지정한다. 해당 값에 따라 web Push 가 결정난다.
     * @param type 메시지의 타입을 지정한다. 알림바에서 구분용도이다.
     * @param reservedTime 알림을 보낼 시간을 지정한다. 예약일 경우 값이 들어간다.
     * @param userId 대상이 되는 유저
     * @param imageLink 알림에 보여질 이미지 링크, 기본적으로 제공하는 것은 peer 의 아이콘이다.
     */
    @Transactional
    @Async
    public void makeNotificationForUser(@Nullable String title,
                                        @NotNull String body,
                                        @NotNull String link,
                                        @NotNull NotificationPriority priority,
                                        @Nullable NotificationType type,
                                        @Nullable LocalDateTime reservedTime,
                                        @NotNull Long userId,
                                        @Nullable String imageLink) {

        // 전체 로직 정리
        /*
          1. 밑작업 준비
          2. 이벤트 생성 - 저장하기
          3. 이벤트에 맞춰 명부 작성 및 저장하기
          4. 이벤트 카운터 등록하기
          5. user 알림 카운터 등록하기
         */
        String url;
        String editedTitle = Objects.requireNonNull(title).isEmpty() ? "peer" : title;

        if (Objects.requireNonNull(imageLink).isEmpty() )
            url = this.DEFAULTICON;
        else
            url = imageLink;

        // 이벤트 생성
        Notification event = Notification.builder()
                .title(editedTitle)
                .body(body)
                .linkData(link)
                .priority(priority)
                .messageType(type)
                .referenceCounter(1)
                .scheduledTime(reservedTime)
                .imageUrl(url)
                .sent(false)
                .build();
        event = this.notificationRepository.save(event);

        // 이벤트 목록 생성
        NotificationTarget target = this.makeOneTargetForEvent(event, type, userId);

        // 이벤트 리스트 저장하기
        this.notificationTargetRepository.save(target);

        // 이벤트 목록의 타겟에 따라서 유저 알림 카운트 올려주기
        this.userRepository.increaseAlarmCountForOne(userId);

        // PWA 업데이트 예정
        if (!event.getPriority().equals(NotificationPriority.SCHEDULED)) {
            //TODO: sent PWA alarm to NestJS
            return;
        }
        // 알림 우선순위 설명
        /*
          IMMEDIATE : 긴급하게 보내야 한다. 알림 권한은 인정한다.
          SCHEDULED : 긴급하게 보낼 필요 없고, 알림 권한은 인정한다.
          FORCE : 긴급하게 보내야 하며, 알림 권한을 무시한다.
         */
    }

    /**
     * 여러 유저를 위한 알림
     * @param title 알림의 타이틀을 지정한다. 지정된 값이 없다면 Default로 peer 가 들어간다.
     * @param body 알림의 메시지 값을 지정한다. 지정된 값이 반드시 필요하다.
     * @param link 알림에서 리다이렉션 들어갈 링크를 지정한다. 반드시 필요하다.
     * @param priority 우선순위를 지정한다. 해당 값에 따라 web Push 가 결정난다.
     * @param type 메시지의 타입을 지정한다. 알림바에서 구분용도이다.
     * @param reservedTime 알림을 보낼 시간을 지정한다. 예약일 경우 값이 들어간다.
     * @param userIds 대상이 되는 유저 목록.
     * @param imageLink 알림에 보여질 이미지 링크, 기본적으로 제공하는 것은 peer 의 아이콘이다.
     */
    @Transactional
    @Async
    public void makeNotificationForUserList(@Nullable String title,
                                            @NotNull String body,
                                            @NotNull String link,
                                            @NotNull NotificationPriority priority,
                                            @Nullable NotificationType type,
                                            @Nullable LocalDateTime reservedTime,
                                            @NotNull List<Long> userIds,
                                            @Nullable String imageLink) {
        String url;
        String editedTitle = Objects.requireNonNull(title).isEmpty() ? "peer" : title;
        if (Objects.requireNonNull(imageLink).isEmpty())
            url = this.DEFAULTICON;
        else
            url = imageLink;

        Notification event = Notification.builder()
                .title(editedTitle)
                .body(body)
                .linkData(link)
                .priority(priority)
                .messageType(type)
                .referenceCounter(userIds.size())
                .scheduledTime(reservedTime)
                .imageUrl(url)
                .sent(false)
                .build();
        event = this.notificationRepository.save(event);

        List<NotificationTarget> targets = this.makeTargetsForEvent(event, type, userIds);

        this.notificationTargetRepository.saveAll(targets);

        this.userRepository.increaseAlarmCountForUsers(userIds);

        if (!event.getPriority().equals(NotificationPriority.SCHEDULED)) {
            //TODO: sent PWA alarm to NestJS
            return;
        }
    }


    /**
     * 한 팀을 위한 알림
     * @param title 알림의 타이틀을 지정한다. 지정된 값이 없다면 Default로 peer 가 들어간다.
     * @param body 알림의 메시지 값을 지정한다. 지정된 값이 반드시 필요하다.
     * @param link 알림에서 리다이렉션 들어갈 링크를 지정한다. 반드시 필요하다.
     * @param priority 우선순위를 지정한다. 해당 값에 따라 web Push 가 결정난다.
     * @param type 메시지의 타입을 지정한다. 알림바에서 구분용도이다.
     * @param reservedTime 알림을 보낼 시간을 지정한다. 예약일 경우 값이 들어간다.
     * @param teamId 대상이 되는 팀
     * @param imageLink 알림에 보여질 이미지 링크, 기본적으로 제공하는 것은 peer 의 아이콘이다.
     */
    @Transactional
    public void makeNotificationForTeam(@Nullable String title,
                                        @NotNull String body,
                                        @NotNull String link,
                                        @NotNull NotificationPriority priority,
                                        @Nullable NotificationType type,
                                        @Nullable LocalDateTime reservedTime,
                                        @NotNull Long teamId,
                                        @Nullable String imageLink)  {
        List<Long> targetUsers = this.makeUserListFromTeamId(teamId);
        this.makeNotificationForUserList(
                title,
                body,
                link,
                priority,
                type,
                reservedTime,
                targetUsers,
                imageLink);
    }


    /**
     * 여러 팀들을 위한 알림
     * @param title 알림의 타이틀을 지정한다. 지정된 값이 없다면 Default로 peer 가 들어간다.
     * @param body 알림의 메시지 값을 지정한다. 지정된 값이 반드시 필요하다.
     * @param link 알림에서 리다이렉션 들어갈 링크를 지정한다. 반드시 필요하다.
     * @param priority 우선순위를 지정한다. 해당 값에 따라 web Push 가 결정난다.
     * @param type 메시지의 타입을 지정한다. 알림바에서 구분용도이다.
     * @param reservedTime 알림을 보낼 시간을 지정한다. 예약일 경우 값이 들어간다.
     * @param teamIds 대상이 되는 팀 목록.
     * @param imageLink 알림에 보여질 이미지 링크, 기본적으로 제공하는 것은 peer 의 아이콘이다.
     */
    @Transactional
    public void makeNotificationForTeams(@Nullable String title,
                                         @NotNull String body,
                                         @NotNull String link,
                                         @NotNull NotificationPriority priority,
                                         @Nullable NotificationType type,
                                         @Nullable LocalDateTime reservedTime,
                                         @NotNull List<Long> teamIds,
                                         @Nullable String imageLink) {
        List<Long> targetUsers = this.makeUserListFromTeamIds(teamIds);
        this.makeNotificationForUserList(
                title,
                body,
                link,
                priority,
                type,
                reservedTime,
                targetUsers,
                imageLink
        );
    }

    /**
     * 전 회원을 위한 알림
     * @param title 알림의 타이틀을 지정한다. 지정된 값이 없다면 Default로 peer 가 들어간다.
     * @param body 알림의 메시지 값을 지정한다. 지정된 값이 반드시 필요하다.
     * @param link 알림에서 리다이렉션 들어갈 링크를 지정한다. 반드시 필요하다.
     * @param priority 우선순위를 지정한다. 해당 값에 따라 web Push 가 결정난다.
     * @param type 메시지의 타입을 지정한다. 알림바에서 구분용도이다.
     * @param reservedTime 알림을 보낼 시간을 지정한다. 예약일 경우 값이 들어간다.
     * @param imageLink 알림에 보여질 이미지 링크, 기본적으로 제공하는 것은 peer 의 아이콘이다.
     */
    @Transactional
    public void makeNotificationForALL(@Nullable String title,
                                       @NotNull String body,
                                       @NotNull String link,
                                       @NotNull NotificationPriority priority,
                                       @Nullable NotificationType type,
                                       @Nullable LocalDateTime reservedTime,
                                       @Nullable String imageLink)  {
        List<Long> userIds = this.userRepository.findAllIds();
        this.makeNotificationForUserList(
                title,
                body,
                link,
                priority,
                type,
                reservedTime,
                userIds,
                imageLink
        );

    }

    /**
     * 모든 알림 목록을 전달한다.예외나 조건 사항은 존재하지 않는다.
     * @return 알림의 리스트를 전달한다.
     */
    public List<Notification> getAllNotificationList() {
        return this.notificationRepository.findAllBy();
    }
    public List<Notification> getActivatedNotificationList() {
        return this.notificationRepository.findAllActivatedBy();
    }

    /**
     * PWA로 예약 발송이 되어 있는 경우의 알림의 리스트를 전체 전달한다. 
     * @return 알림의 리스트를 전달한다.
     */
    public List<Notification> getReservedNotificationList() {
        return this.notificationRepository.findAllNotSentBy();
    }

    /**
     * 키워드를 입력하여 타이틀 또는 본문 내용을 검색하여 해당 키워드가 포함된 알림만을 전달한다. 
     * @param keyword 2자 이상의 글자를 넣으면 된다.
     * @return 성공하면 성공한 이벤트 리스트, 실패하면 Null 을 반환한다.
     */
    public List<Notification> searchNotificationByKeyword(String keyword) {
        if(keyword.length() < 2)
            return null;
        return this.notificationRepository.findByKeyword(keyword);
    }

    /**
     * 기준이 되는 날을 넣으면 이에 대해 알림을 검색해낸다.
     * @param date 기준이 되는 날짜를 입력한다.
     * @return 해당하는 날짜 이후에 알림 목록을 전달한다.
     */
    public List<Notification> searchNotificationAfterThisDay(LocalDateTime date) {
        return this.notificationRepository.findByCreatedAtAfter(date);
    }

    /**
     * 기준이 되는 날을 넣으면 이에 대해 알림을 검색해낸다.
     * @param date 기준이 되는 날짜를 입력한다.
     * @return 해당하는 날짜 이후에 알림 목록을 전달한다.
     */
    public List<Notification> searchNotificationBeforeThisDay(LocalDateTime date) {
        return this.notificationRepository.findByCreatedAtBefore(date);
    }

    public Notification getNotificationById(Long eventId) {
        return this.notificationRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException("해당 이벤트는 존재하지 않습니다."));
    }

    /**
     * 이벤트 Id를 통해 해당하는 유저 목록을 전달한다.
     * @param eventId 이벤트의 구분자를 전달한다.
     * @return 유저의 목록을 반환한다.
     */
    public List<User> getUserListFromEvent(Long eventId) {
        List<String> targets =  this.notificationTargetRepository.findUserListById(eventId);
        List<Long> userList = targets.stream()
                .flatMap(s -> Arrays.stream(s.split(DEFAULT_DELIMITTER)))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return this.userRepository.findByIdIn(userList);
    }


    //TODO: 갱신 로직 고민할 것, 갱신할 대상은? 유저? eventId?
    // 필요한 갱신의 종류는?
    // 1. 예약 대상의 취소
    // 2. 예약된 알림 자체의 취소
    // 3. 내용 변경 (타이틀, 바디, 링크)
    // 4. 

    //TODO: 알림 권한 받는 컨트롤러
    //TODO: 새 알림 여부 확인 api
    //TODO: 각종 탭에서 알림 요청에 맞춰 전달 api
    //TODO: 알림들 각종 api에 적용
    //

    /**
     * 이벤트 Id 기준으로 해당하는 알림을 삭제한다.
     * @param eventId 이벤트 구분자
     */
    public void deleteNotificationById(Long eventId) {
        this.notificationRepository.deleteById(eventId);
    }
}
