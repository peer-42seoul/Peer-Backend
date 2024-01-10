package peer.backend.repository.noti;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.noti.old.NotificationSubscription;

public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, Long> {
}
