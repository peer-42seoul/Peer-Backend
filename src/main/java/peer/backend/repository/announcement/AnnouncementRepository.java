package peer.backend.repository.announcement;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.announcement.Announcement;
import peer.backend.entity.announcement.AnnouncementStatus;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    public List<Announcement> findAllByAnnouncementStatus(AnnouncementStatus status);
}
