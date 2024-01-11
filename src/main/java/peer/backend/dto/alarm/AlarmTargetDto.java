package peer.backend.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.noti.Notification;
import peer.backend.entity.noti.enums.AlarmType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AlarmTargetDto {
    private Long userId;
    private Notification alarm;
    private AlarmType alarmType;
    private Boolean read;
    private Boolean deleted;
}
