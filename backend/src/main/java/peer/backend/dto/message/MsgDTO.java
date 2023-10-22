package peer.backend.dto.message;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgDTO {
    private long senderId;
    private String senderNickname;
    private String targetProfile; // TODO: 이거 고쳐야 함. 데이터 사용에 오버헤드가 심함.
    private long msgId;
    private String content;
    private String date;
    private boolean isEnd;
}
