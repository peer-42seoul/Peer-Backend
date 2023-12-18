package peer.backend.repository.alarm;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import peer.backend.entity.alarm.Alarm;
import peer.backend.entity.alarm.enums.AlarmType;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("SELECT a FROM Alarm a INNER JOIN AlarmTarget at ON a.id = at.alarm.id " +
            "WHERE at.target = :userId AND at.alarmType = :alarmType")
    List<Alarm> findByUserIdAndAlarmType(@Param("userId") Long userId, @Param("alarmType") AlarmType alarmType);

}