package peer.backend.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.alarm.Alarm;
import peer.backend.entity.alarm.enums.AlarmType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AlarmTargetDto {
    private Long userId;
    private Alarm alarm;
    private AlarmType alarmType;
    private Boolean read;
    private Boolean deleted;
}
