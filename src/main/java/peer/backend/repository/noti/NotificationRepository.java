package peer.backend.repository.noti;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.noti.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
