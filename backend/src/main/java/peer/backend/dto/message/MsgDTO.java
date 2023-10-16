package peer.backend.dto.message;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgDTO {
    private long senderId;
    private String senderNickname;
    private String targetProfile;
    private long msgId;
    private String content;
    private String date;
    private boolean isEnd;
}
