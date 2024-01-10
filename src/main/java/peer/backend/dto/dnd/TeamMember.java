package peer.backend.dto.dnd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {
    private Long teamId;
    private Long userId;
    // TeamUserStatus approved 된 사람들만을 리스트 화 하여 넣어 둬야 한다.
}
