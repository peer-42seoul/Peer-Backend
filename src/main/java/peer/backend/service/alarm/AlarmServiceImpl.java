package peer.backend.service.alarm;

import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.dto.alarm.AlarmTargetDto;
import peer.backend.entity.alarm.Alarm;
import peer.backend.entity.alarm.AlarmTarget;
import peer.backend.entity.alarm.enums.AlarmType;
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
    public Alarm saveAlarm(AlarmDto data) {
        return alarmRepository.save(alarmFromDto(data));
    }

    @Override
    public AlarmTarget saveAlarmTarget(Alarm alarm) {

        AlarmTargetDto alarmTargetDto = AlarmTargetDto.builder()
                .userId(alarm.getTarget())
                .alarm(alarm)
                .alarmType(AlarmType.GENERAL)
                .build();
        AlarmTarget alarmTarget = alarmTargetFromDto(alarmTargetDto);
        return alarmTargetRepository.save(alarmTarget);
    }

    @Override
    public AlarmTarget alarmTargetFromDto(AlarmTargetDto dto) {
        return AlarmTarget.builder()
                .target(dto.getUserId())
                .alarm(dto.getAlarm())
                .alarmType(dto.getAlarmType())
                .read(false)
                .deleted(false)
                .build();
    }
    @Override
    public Alarm alarmFromDto(AlarmDto dto) {

        return Alarm.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .targetType(dto.getTargetType())
                .target(dto.getTarget())
                .link(dto.getLink())
                .sent(false)
                .priority(dto.getPriority())
                .scheduledTime(new Date())
                .build();
    }

    @Override
    public List<Alarm> getAlarm(Long target) {
        return null;
    }

    @Override
    public List<Alarm> getAlarmGeneral(Long target) {
        return alarmRepository.findByUserIdAndAlarmType(target, AlarmType.GENERAL);
    }

    @Override
    public void deleteAlarm(Long id) {
        alarmRepository.deleteById(id);
    }
}
