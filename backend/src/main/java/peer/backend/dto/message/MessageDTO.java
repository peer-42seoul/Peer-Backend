package peer.backend.dto.message;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.message.MessageType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private String nickname;
    private String content;
    private LocalDateTime messageTime;
    private MessageType messageType;
}
