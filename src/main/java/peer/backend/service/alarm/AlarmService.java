package peer.backend.service.alarm;

import java.util.List;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.entity.alarm.Alarm;

public interface AlarmService {
    public void saveAlarm(Alarm data);

    public Alarm alarmFromDto(AlarmDto dto);

    public List<Alarm> getAlarm(Long target);

    public List<Alarm> getAlarmGeneral();
    public void deleteAlarm(Long id);
}
