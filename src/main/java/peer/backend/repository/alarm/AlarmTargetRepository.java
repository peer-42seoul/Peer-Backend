package peer.backend.repository.alarm;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.alarm.AlarmTarget;

public interface AlarmTargetRepository extends JpaRepository<AlarmTarget, Long> {

}
