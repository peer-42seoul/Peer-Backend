package peer.backend.dto.message;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Msg {
    private long userId;
    private long msgId;
    private String content;
    private String date;
    private boolean isEnd;
}
