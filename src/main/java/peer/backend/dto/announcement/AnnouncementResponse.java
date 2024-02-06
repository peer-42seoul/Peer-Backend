package peer.backend.dto.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.Getter;
import peer.backend.entity.announcement.Announcement;
import peer.backend.entity.announcement.AnnouncementStatus;

@Getter
public class AnnouncementResponse {

    private final String title;
    private final String writer;
    private final String content;
    private final String image;
    private final Long view;
    private final AnnouncementStatus announcementStatus;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private final LocalDateTime date;

    public AnnouncementResponse(Announcement announcement) {
        this.title = announcement.getTitle();
        this.writer = announcement.getWriter();
        this.content = announcement.getContent();
        this.image = announcement.getImage();
        this.view = announcement.getView();
        this.announcementStatus = announcement.getAnnouncementStatus();
        if (this.announcementStatus.equals(AnnouncementStatus.RESERVATION)) {
            this.date = announcement.getReservationDate();
        } else {
            this.date = announcement.getCreatedAt();
        }
    }
}
