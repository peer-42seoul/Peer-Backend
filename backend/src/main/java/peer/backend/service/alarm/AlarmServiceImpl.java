package peer.backend.service.alarm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.entity.alarm.Alarm;
import peer.backend.entity.alarm.enums.TargetType;
import peer.backend.repository.alarm.AlarmRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlarmServiceImpl implements AlarmService{
    private final AlarmRepository alarmRepository;
    @Override
    public void saveAlarm(Alarm data) {
        alarmRepository.save(data);
    }

/*
    title;
    message;
    TargetType;
    target;
    link;
    sent;
    Priority;
 */
    @Override
    public Alarm AlarmFromDto(AlarmDto dto, Long target) {

        return Alarm.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .targetType(TargetType.CERTAIN)
                .target(target)
                .link(dto.getLink())
                .sent(false)
                .priority(dto.getPriority())
                .build();
    }

    @Override
    public Alarm AlarmFromDto(AlarmDto dto) {
        return Alarm.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .targetType(TargetType.ALL)
                .target(0L)
                .link(dto.getLink())
                .sent(false)
                .priority(dto.getPriority())
                .build();
    }
}
