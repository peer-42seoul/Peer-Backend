package peer.backend.dto;

import java.util.List;
import lombok.Getter;
import peer.backend.entity.NoticeTargetType;

@Getter
public class NoticeResponse {

    private Long noticeId;
    private String title;
    private String content;
    private NoticeTargetType noticeTargetType;
    private List<NoticeResponseContainable> targetList;
}
