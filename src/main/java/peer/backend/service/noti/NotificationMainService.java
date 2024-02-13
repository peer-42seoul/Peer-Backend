package peer.backend.service.noti;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import peer.backend.dto.noti.NotificationDTO;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.entity.noti.Notification;
import peer.backend.entity.noti.NotificationTarget;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.repository.noti.NotificationRepository;
import peer.backend.repository.noti.NotificationSubscriptionKeysRepsitory;
import peer.backend.repository.noti.NotificationTargetRepository;
import peer.backend.repository.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

// 포어 그라운드에서 동작하는 내용들을 담아야 하는 서비스
// 사실상 외부에서 들어오는 내용에 대해 구동 가능하게 하는 서비스
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationMainService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;
    private final NotificationSubscriptionKeysRepsitory notificationSubscriptionKeysRepsitory;
    private final NotificationCreationService notificationCreationService;


    @Transactional
    public void setAlarmForUser(User user,String type, boolean value) {
        switch (type) {
            case "keyword":
                user.setKeywordRecommendAlarm(value);
                break;
            case "team":
                user.setTeamAlarm(value);
                break;
            case "message":
                user.setMessageAlarm(value);
                break;
            default:
                user.setNightAlarm(value);
                break;
        }
        userRepository.save(user);
    }

    @Transactional
    public List<NotificationDTO> getNotificationList(User user,
                                                     NotificationType type,
                                                     Long pgIndex,
                                                     Long size) {
        List<NotificationDTO> result = new ArrayList<>();
        if (user.getAlarmCounter() == 0)
            return Collections.emptyList();

        // redis 적용
        // 신규 내용이 없으면(newAlarmCounter = 0) 키 = userId + pgIndex + size / value = List<NotificationDTO>
        // 신규 내용이 있으면, 그냥 탐색 및 제작 및 등록

        List<Notification> specificEventList;
        if (type.equals(NotificationType.ALL))
        {
            specificEventList = this.notificationTargetRepository.getAllEventsByColumnIndexAndUserId(
                    user.getId() / 100,
                    user.getId().toString(),
                    LocalDateTime.now()
            );
        }
        else {
            specificEventList = this.notificationTargetRepository
                    .getAllEventsByColumnIndexAndUserIdAndMessageType(user.getId() / 100,
                            user.getId().toString(),
                            type,
                            LocalDateTime.now());
        }
        int start = (int) (pgIndex * size - size);
        int end = (int) (start + size - 1);

        if (specificEventList.size() < start)
            throw new BadRequestException("잘못된 요청입니다.");

        for (int i = start; i < specificEventList.size(); i++) {
            if (i == end + 1)
                break ;
            if (i + 1 == specificEventList.size()) {
                // 마지막 만들기 및 추가
                NotificationDTO data = new NotificationDTO(specificEventList.get(i), true);
                result.add(data);
                break;
            }
            NotificationDTO data = new NotificationDTO(specificEventList.get(i), false);
            result.add(data);
        }

        user.setNewAlarmCounter(0);
        this.userRepository.save(user);
        return result;
    }

    @Transactional
    public void deleteNotificationAll(User user, NotificationType type) {
        List<NotificationTarget> targetList;
        List<NotificationTarget> deleteTarget = new ArrayList<>();
        AtomicReference<Integer> counter = new AtomicReference<>(user.getAlarmCounter());
        if (type.equals(NotificationType.ALL)) {
            targetList = this.notificationTargetRepository.findAllByColumnIndexAndUserId(user.getId() / 100 ,
                    user.getId().toString());
        } else {
            targetList = this.notificationTargetRepository.findByColumnIndexAndUserIdAndType(
                    user.getId() / 100,
                    user.getId().toString(),
                    type);
        }
        targetList.forEach(target -> {
            target.deleteUserId(user.getId());
            if (target.getUserList().isEmpty())
                deleteTarget.add(target);
            counter.getAndSet(counter.get() - 1);
        });
        user.setAlarmCounter(counter.get());
        this.notificationTargetRepository.deleteAll(deleteTarget);
        this.userRepository.save(user);
    }

    @Transactional
    public void deleteNotification(User user, Long eventId) {
        NotificationTarget target = this.notificationTargetRepository.findByColumnIndexAndEventId(
                user.getId() / 100,
                eventId).orElseThrow(() -> new BadRequestException("해당하는 이벤트를 발견할 수 없습니다."));
        target.deleteUserId(user.getId());
        if (target.getUserList().isEmpty())
            this.notificationTargetRepository.delete(target);
        Integer value = user.getAlarmCounter() - 1;
        user.setAlarmCounter(value);
        this.userRepository.save(user);
    }
}
