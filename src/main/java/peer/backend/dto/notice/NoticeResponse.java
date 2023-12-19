package peer.backend.dto.notice;

import lombok.Getter;
import peer.backend.entity.notice.Notice;
import peer.backend.entity.notice.NoticeStatus;

@Getter
public class NoticeResponse {

    private Long noticeId;
    private NoticeStatus noticeStatus;
    private String title;
    private String image;

    public NoticeResponse(Notice notice) {
        this.noticeId = notice.getId();
        this.noticeStatus = notice.getStatus();
        this.title = notice.getTitle();
        this.image = notice.getImage();
    }
}
