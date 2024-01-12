package peer.backend.repository.noti;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.noti.NotificationSubscriptionKeys;

public interface NotificationSubscriptionKeysRepsitory extends JpaRepository<NotificationSubscriptionKeys, Long> {
}
