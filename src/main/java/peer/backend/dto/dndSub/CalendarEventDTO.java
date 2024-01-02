package peer.backend.dto.dndSub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarEventDTO target = (CalendarEventDTO) o;
        return Objects.equals(eventId, ((CalendarEventDTO) o).eventId) &&
                Objects.equals(teamId, ((CalendarEventDTO) o).teamId);
    }
    // HashSet 을 제대로 동작시키기 위한 코드, 같은지 여부를 다각도로 점검한다.

    @Override
    public int hashCode() {
        return Objects.hash(teamId, eventId);
    }
}
