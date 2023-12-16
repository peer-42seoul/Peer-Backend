package peer.backend.service.alarm;

import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.entity.alarm.Alarm;
import peer.backend.entity.alarm.enums.TargetType;
import peer.backend.repository.alarm.AlarmRepository;
import peer.backend.repository.alarm.AlarmTargetRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlarmServiceImpl implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final AlarmTargetRepository alarmTargetRepository;
    @Override
    public void saveAlarm(Alarm data) {
        alarmRepository.save(data);
    }

    @Override
    public Alarm alarmFromDto(AlarmDto dto) {

        return Alarm.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .targetType(dto.getTargetType())
                .target(0L)
                .link(dto.getLink())
                .sent(false)
                .priority(dto.getPriority())
                .scheduledTime(new Date())
                .build();
    }

    @Override
    public List<Alarm> getAlarm(Long target) {
        return alarmRepository.findByTarget(target);
    }

    @Override
    public List<Alarm> getAlarmGeneral() {
        return alarmRepository.findByTarget(0L);
    }

    @Override
    public void deleteAlarm(Long id) {
        alarmRepository.deleteById(id);
    }
}
