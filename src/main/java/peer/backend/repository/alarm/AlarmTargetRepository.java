package peer.backend.repository.alarm;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import peer.backend.entity.alarm.AlarmTarget;

@Repository
public interface AlarmTargetRepository extends JpaRepository<AlarmTarget, Long> {
    @Query("SELECT e FROM AlarmTarget e WHERE e.alarm.target = :target ORDER BY e.createdAt DESC")
    List<AlarmTarget> findAlarmTargetByTarget(Long target);
}
