package peer.backend.repository.noti;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.noti.Notification;
import peer.backend.entity.noti.NotificationSubscription;

public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, Long> {
}
