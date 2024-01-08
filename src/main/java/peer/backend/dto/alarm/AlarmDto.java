package peer.backend.dto.alarm;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.noti.enums.Priority;
import peer.backend.entity.noti.enums.TargetType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AlarmDto {
    private String title;
    private String message;
    private TargetType targetType;
    private Long target;
    private String link;
    private Priority priority;
    private Date scheduledTime;
}
