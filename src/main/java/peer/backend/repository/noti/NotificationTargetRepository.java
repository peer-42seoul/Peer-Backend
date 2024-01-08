package peer.backend.repository.noti;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.noti.NotificationTarget;

public interface NotificationTargetRepository extends JpaRepository<NotificationTarget, Long> {

}
