package peer.backend.dto.message;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgObjectDTO {
    private long targetId;
    private String targetNickname;
    private String targetProfile;
    private long conversationId;
    private long unreadMsgNumber;
    private long MsgId;
    private String latestContent;
    private String latestDate;
}
