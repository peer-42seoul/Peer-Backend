package peer.backend.service.noti;

import java.util.List;
import peer.backend.dto.noti.AlarmDto;
import peer.backend.dto.noti.AlarmTargetDto;
import peer.backend.entity.noti.Notification;
import peer.backend.entity.noti.NotificationTarget;

public interface NotificationService {
    public Notification saveAlarm(AlarmDto data);

    public NotificationTarget saveAlarmTarget(Notification dto);

    public NotificationTarget alarmTargetFromDto(AlarmTargetDto dto);

    public Notification alarmFromDto(AlarmDto dto);

    public List<Notification> getAlarm(Long target);

    public List<Notification> getAlarmGeneral(Long target);
    public void deleteAlarm(Long id);
}
