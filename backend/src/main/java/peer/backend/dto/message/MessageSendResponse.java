package peer.backend.dto.message;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageSendResponse {
    private String content;
    private LocalDateTime messageTime;
    private String senderNickname;
    private String receiverNickname;
}
