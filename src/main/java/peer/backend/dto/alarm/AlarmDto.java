package peer.backend.dto.alarm;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.alarm.enums.Priority;
import peer.backend.entity.alarm.enums.TargetType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AlarmDto {
    private String title;
    private String message;
    private TargetType targetType;
    private List<Long> target;
    private String link;
    private Priority priority;
    private Date scheduledTime;
}
