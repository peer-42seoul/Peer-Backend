package peer.backend.repository.noti;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.noti.old.Notification;
import peer.backend.entity.noti.old.enums.AlarmType;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT a FROM Notification a INNER JOIN NotificationTarget at ON a.notificationId = at.specificNoti.notificationId" +
            " " +
            "WHERE at.target = :userId AND at.alarmType = :alarmType")
    List<Notification> findByUserIdAndAlarmType(@Param("userId") Long userId, @Param("alarmType") AlarmType alarmType);

}