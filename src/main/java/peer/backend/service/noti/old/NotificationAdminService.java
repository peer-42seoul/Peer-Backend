package peer.backend.service.noti.old;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.noti.old.AlarmDto;
import peer.backend.entity.noti.old.Notification;
import peer.backend.repository.noti.old.NotificationRepository;
import peer.backend.repository.noti.old.NotificationTargetRepository;

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
