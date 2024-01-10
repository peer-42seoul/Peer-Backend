package peer.backend.service.noti.old;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.noti.old.AlarmDto;
import peer.backend.dto.noti.old.AlarmTargetDto;
import peer.backend.entity.noti.old.Notification;
import peer.backend.entity.noti.old.NotificationTarget;
import peer.backend.entity.noti.old.enums.AlarmType;
import peer.backend.repository.noti.NotificationRepository;
import peer.backend.repository.noti.NotificationTargetRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;
    @Override
    public Notification saveAlarm(AlarmDto data) {
        return notificationRepository.save(alarmFromDto(data));
    }

    @Override
    public NotificationTarget saveAlarmTarget(Notification notification) {

        AlarmTargetDto alarmTargetDto = AlarmTargetDto.builder()
                .userId(notification.getTarget())
                .alarm(notification)
                .alarmType(AlarmType.GENERAL)
                .build();
        NotificationTarget notificationTarget = alarmTargetFromDto(alarmTargetDto);
        return notificationTargetRepository.save(notificationTarget);
    }

    @Override
    public NotificationTarget alarmTargetFromDto(AlarmTargetDto dto) {
        return NotificationTarget.builder()
                .target(dto.getUserId())
                .specificNoti(dto.getAlarm())
                .alarmType(dto.getAlarmType())
                .checked(false)
                .deleted(false)
                .build();
    }
    @Override
    public Notification alarmFromDto(AlarmDto dto) {

        return Notification.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .targetType(dto.getTargetType())
                .target(dto.getTarget())
                .link(dto.getLink())
                .sent(false)
                .priority(dto.getPriority())
                .scheduledTime(LocalDateTime.now())
                .build();
    }

    @Override
    public List<Notification> getAlarm(Long target) {
        return null;
    }

    @Override
    public List<Notification> getAlarmGeneral(Long target) {
        return notificationRepository.findByUserIdAndAlarmType(target, AlarmType.GENERAL);
    }

    @Override
    public void deleteAlarm(Long id) {
        notificationRepository.deleteById(id);
    }
}
