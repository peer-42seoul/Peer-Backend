package peer.backend.repository.announcement;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.announcement.Announcement;
import peer.backend.entity.announcement.AnnouncementStatus;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findAllByAnnouncementStatus(AnnouncementStatus status);

    Page<Announcement> findAllByAnnouncementStatus(AnnouncementStatus status, Pageable pageable);
}
