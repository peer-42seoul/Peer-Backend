package peer.backend.repository.noti.old;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.noti.old.NotificationTarget;

public interface NotificationTargetRepository extends JpaRepository<NotificationTarget, Long> {

}
