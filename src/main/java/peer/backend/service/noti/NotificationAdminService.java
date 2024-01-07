package peer.backend.service.noti;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.entity.noti.Notification;
import peer.backend.repository.noti.NotificationRepository;
import peer.backend.repository.noti.NotificationTargetRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationAdminService {
    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;
    public AlarmDto getQueueAlarm() {
        return null;
    }


    public List<Notification> getAlarm(String type) {
        return null;
    }
}
