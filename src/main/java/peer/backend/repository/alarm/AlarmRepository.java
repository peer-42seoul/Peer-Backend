package peer.backend.repository.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import peer.backend.entity.alarm.Alarm;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {


    @Query("SELECT e FROM Alarm e WHERE e.target = :target ORDER BY e.scheduledTime DESC")
    List<Alarm> findByTarget(Long target);


}
