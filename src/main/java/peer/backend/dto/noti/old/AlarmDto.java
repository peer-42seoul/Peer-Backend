package peer.backend.dto.noti.old;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.noti.old.enums.Priority;
import peer.backend.entity.noti.old.enums.TargetType;

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
