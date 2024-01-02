package peer.backend.dto.dnd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDnDDTO {
    private Long teamId;
    private String type;
}
