package peer.backend.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpecificMsgDTO {
    private long targetId;
    private long conversationalId;
}
