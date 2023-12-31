package peer.backend.dto.dndSub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDTO {
    private Long teamId;
    private Long eventId;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private List<MemberDTO> member;
}
