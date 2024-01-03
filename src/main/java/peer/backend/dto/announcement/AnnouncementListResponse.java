package peer.backend.dto.announcement;

import lombok.Getter;
import peer.backend.entity.announcement.Announcement;
import peer.backend.entity.announcement.AnnouncementStatus;

@Getter
public class AnnouncementListResponse {

    private final Long announcementId;
    private final AnnouncementStatus announcementStatus;
    private final String title;
    private final String image;

    public AnnouncementListResponse(Announcement announcement) {
        this.announcementId = announcement.getId();
        this.announcementStatus = announcement.getAnnouncementStatus();
        this.title = announcement.getTitle();
        this.image = announcement.getImage();
    }
}
