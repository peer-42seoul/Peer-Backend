package peer.backend.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpecificScrollMsgDTO {
    private long targetId;
    private long conversationId;
    private long earlyMsgId;
}
