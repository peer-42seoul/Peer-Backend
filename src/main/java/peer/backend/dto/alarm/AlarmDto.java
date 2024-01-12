package peer.backend.dto.alarm;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.noti.enums.Priority;
import peer.backend.dto.noti.enums.TargetType;

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
    private LocalDateTime scheduledTime;
}
