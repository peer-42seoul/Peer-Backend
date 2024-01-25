package peer.backend.dto.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.Getter;
import peer.backend.entity.announcement.Announcement;

@Getter
public class AboutAnnouncementListResponse {

    private Long id;

    private String title;

    private String writer;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime date;

    public AboutAnnouncementListResponse(Announcement announcement) {
        this.id = announcement.getId();
        this.title = announcement.getTitle();
        this.writer = announcement.getWriter();
        this.date = announcement.getCreatedAt();
    }
}
