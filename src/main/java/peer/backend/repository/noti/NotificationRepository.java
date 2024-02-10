package peer.backend.repository.noti;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.noti.Notification;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

//    @Query("SELECT COUNT(n.id) = :size FROM Notification n WHERE n.id IN :ids")
//    boolean existsAllByIdIn(@Param("ids") List<Long> ids, @Param("size") long size);

    @Query("SELECT e FROM Notification e WHERE e.title Like :keyword OR e.body Like :keyword")
    List<Notification> findByKeyword(@Param("keyword") String keyword);

    List<Notification> findByCreatedAtAfter(LocalDateTime time);

    List<Notification> findByCreatedAtBefore(LocalDateTime time);

    List<Notification> findAllBy();

    @Query("SELECT m FROM Notification m WHERE m.referenceCounter != 0")
    List<Notification> findAllActivatedBy();

    @Query("SELECT m FROM Notification m WHERE m.sent = false AND m.referenceCounter != 0")
    List<Notification> findAllNotSentBy();
}
