package peer.backend.repository.noti;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.noti.NotificationTarget;

import java.util.List;

public interface NotificationTargetRepository extends JpaRepository<NotificationTarget, Long> {

    @Query("SELECT m.userList FROM NotificationTarget m WHERE m.notificationId = :eventId")
    List<String> findUserListById(@Param("eventId") Long eventId);
}
