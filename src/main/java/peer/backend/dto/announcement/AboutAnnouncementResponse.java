package peer.backend.dto.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.Getter;
import peer.backend.entity.announcement.Announcement;

@Getter
public class AboutAnnouncementResponse {

    private String title;

    private String writer;

    private String content;

    private Long view;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    public AboutAnnouncementResponse(Announcement announcement) {
        this.title = announcement.getTitle();
        this.writer = announcement.getWriter();
        this.content = announcement.getContent();
        this.view = announcement.getView();
        this.createdAt = announcement.getCreatedAt();
        this.updatedAt = announcement.getUpdatedAt();
    }
}
