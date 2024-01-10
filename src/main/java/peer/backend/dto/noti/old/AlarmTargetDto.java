package peer.backend.dto.noti.old;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.noti.old.Notification;
import peer.backend.entity.noti.old.enums.AlarmType;

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
