package peer.backend.service.noti.old;

import java.util.List;
import peer.backend.dto.noti.old.AlarmDto;
import peer.backend.dto.noti.old.AlarmTargetDto;
import peer.backend.entity.noti.old.Notification;
import peer.backend.entity.noti.old.NotificationTarget;

public interface NotificationService {
    public Notification saveAlarm(AlarmDto data);

    public NotificationTarget saveAlarmTarget(Notification dto);

    public NotificationTarget alarmTargetFromDto(AlarmTargetDto dto);

    public Notification alarmFromDto(AlarmDto dto);

    public List<Notification> getAlarm(Long target);

    public List<Notification> getAlarmGeneral(Long target);
    public void deleteAlarm(Long id);
}
