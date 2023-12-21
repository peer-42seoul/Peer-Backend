package peer.backend.dto.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.Getter;
import peer.backend.entity.notice.Notice;
import peer.backend.entity.notice.NoticeStatus;

@Getter
public class NoticeResponse {

    private final String title;
    private final String writer;
    private final String content;
    private final String image;
    private final Long view;
    private final NoticeStatus noticeStatus;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime date;

    public NoticeResponse(Notice notice) {
        this.title = notice.getTitle();
        this.writer = notice.getWriter();
        this.content = notice.getContent();
        this.image = notice.getImage();
        this.view = notice.getView();
        this.noticeStatus = notice.getStatus();
        if (this.noticeStatus.equals(NoticeStatus.RESERVATION)) {
            this.date = notice.getReservationDate();
        } else {
            this.date = notice.getCreatedAt();
        }
    }
}
