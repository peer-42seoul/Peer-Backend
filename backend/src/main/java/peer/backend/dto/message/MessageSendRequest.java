package peer.backend.dto.message;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendRequest {
    private Long userId;
    private String content;
    private LocalDateTime messageTime;
}
