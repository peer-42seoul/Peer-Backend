package peer.backend.service.alarm;

import java.util.List;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.dto.alarm.AlarmTargetDto;
import peer.backend.entity.alarm.Alarm;
import peer.backend.entity.alarm.AlarmTarget;

public interface AlarmService {
    public Alarm saveAlarm(AlarmDto data);

    public AlarmTarget saveAlarmTarget(Alarm dto);

    public AlarmTarget alarmTargetFromDto(AlarmTargetDto dto);

    public Alarm alarmFromDto(AlarmDto dto);

    public List<Alarm> getAlarm(Long target);

    public List<Alarm> getAlarmGeneral(Long target);
    public void deleteAlarm(Long id);
}
