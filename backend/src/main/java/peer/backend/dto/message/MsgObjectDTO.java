package peer.backend.dto.message;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgObjectDTO {
    private long targetId; // User Primary Key
    private String targetNickname;
    private String targetProfile; // URL
    private long conversationId; // 대화의 Indexing 용 고유키
    private long unreadMsgNumber; // 본인 기준 읽지 않은 메시지 수
    private long latestMsgId; // 가장 최신 메시지의 고유 ID
    private String latestContent; // 가장 최신의 메시지
    private String latestDate; // 가장 최신의 메시지의 날짜
}
