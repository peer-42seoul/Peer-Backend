package peer.backend.dto.message;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MsgContentDTO {
    private long targetId;
    private String content;
}
