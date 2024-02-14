package peer.backend.repository.noti;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.entity.noti.Notification;
import peer.backend.entity.noti.NotificationTarget;

import java.util.List;
import java.util.Optional;

public interface NotificationTargetRepository extends JpaRepository<NotificationTarget, Long> {

    @Query("SELECT m.userList FROM NotificationTarget m WHERE m.notificationId = :eventId")
    List<String> findUserListById(@Param("eventId") Long eventId);


    @Query("SELECT m.specificEvent FROM NotificationTarget m WHERE m.columnIndex = :columnIndex AND m.userList LIKE %:userId% AND m.messageType = :type ORDER BY m.createdAt DESC")
    List<Notification> getAllEventsByColumnIndexAndUserIdAndMessageType(@Param("columnIndex") Long columnIndex,
                                                                        @Param("userId") String userId,
                                                                        @Param("type") NotificationType type);

    @Query("SELECT m.specificEvent FROM NotificationTarget m WHERE m.columnIndex = :columnIndex AND m.userList LIKE %:userId% ORDER BY m.createdAt DESC")
    List<Notification> getAllEventsByColumnIndexAndUserId(@Param("columnIndex") Long columnIndex,
                                                          @Param("userId") String userId);

    @Query("SELECT m FROM NotificationTarget m WHERE m.columnIndex = :columnIndex AND m.userList LIKE %:userId% ORDER BY m.createdAt DESC")
    List<NotificationTarget> findAllByColumnIndexAndUserId(@Param("columnIndex") Long columnIndex,
                                                           @Param("userId") String userId);

    @Query("SELECT m FROM NotificationTarget m WHERE m.columnIndex = :columnIndex AND m.userList LIKE %:userId% AND m.messageType = :type ORDER BY m.createdAt DESC")
    List<NotificationTarget> findByColumnIndexAndUserIdAndType(@Param("columnIndex") Long columnIndex,
                                                               @Param("userId") String userId,
                                                               @Param("type") NotificationType type);

    @Query("SELECT m FROM NotificationTarget m WHERE m.columnIndex = :columnIndex AND m.notificationId = :eventId")
    Optional<NotificationTarget> findByColumnIndexAndEventId(@Param("columnIndex") Long columnIndex,
                                                             @Param("eventId") Long eventId);
}
