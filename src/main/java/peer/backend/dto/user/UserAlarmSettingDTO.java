package peer.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserAlarmSettingDTO {
    private boolean keyword;
    private boolean team;
    private boolean message;
    private boolean nightAlarm;
}
