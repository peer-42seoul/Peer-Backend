package peer.backend.service.alarm;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.entity.alarm.Alarm;
import peer.backend.repository.alarm.AlarmRepository;
import peer.backend.repository.alarm.AlarmTargetRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlarmAdminService {
    private final AlarmRepository alarmRepository;
    private final AlarmTargetRepository alarmTargetRepository;
    public AlarmDto getQueueAlarm() {
        return null;
    }


    public List<Alarm> getAlarm(String type) {
        return null;
    }
}
