package peer.backend.repository.announcement;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.announcement.Announcement;
import peer.backend.entity.announcement.AnnouncementStatus;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findAllByAnnouncementStatus(AnnouncementStatus status);

    //    @Query("SELECT m FROM Announcement m WHERE m.announcementStatus = :status")
    Page<Announcement> findAllByAnnouncementStatusOrderByCreatedAtDesc(AnnouncementStatus status,
        Pageable pageable);

    Page<Announcement> findAllByOrderByIdDesc(Pageable pageable);
}
