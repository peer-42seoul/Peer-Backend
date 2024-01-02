package peer.backend.dto.notice;

import lombok.Getter;
import peer.backend.entity.notice.Notice;
import peer.backend.entity.notice.NoticeStatus;

@Getter
public class NoticeListResponse {

    private final Long noticeId;
    private final NoticeStatus noticeStatus;
    private final String title;
    private final String image;

    public NoticeListResponse(Notice notice) {
        this.noticeId = notice.getId();
        this.noticeStatus = notice.getNoticeStatus();
        this.title = notice.getTitle();
        this.image = notice.getImage();
    }
}
