package peer.backend.dto.adminAlarm;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AlarmIdRequest {

    @NotNull(message = "알림 Id는 필수입니다!")
    private Long alarmId;
}
