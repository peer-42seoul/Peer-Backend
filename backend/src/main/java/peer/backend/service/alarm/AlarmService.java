package peer.backend.service.alarm;

import peer.backend.dto.alarm.AlarmDto;
import peer.backend.entity.alarm.Alarm;

public interface AlarmService {
    public void saveAlarm(Alarm data);

    public Alarm AlarmFromDto(AlarmDto dto, Long target);
    public Alarm AlarmFromDto(AlarmDto dto);

}
