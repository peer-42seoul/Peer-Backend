package peer.backend.service.alarm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.entity.alarm.Alarm;
import peer.backend.entity.alarm.enums.Priority;
import peer.backend.entity.alarm.enums.TargetType;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamService Test")
class AlarmServiceTest {

    @InjectMocks
    private AlarmServiceImpl alarmService;

    AlarmDto getAlarmDtoSupport() {
        return AlarmDto.builder()
                .title("test")
                .link("test")
                .message("test")
                .priority(Priority.SCHEDULED)
                .sent(false)
                .build();
    }

    @Test
    void testCertainAlarmFromDto() {
        AlarmDto dto = getAlarmDtoSupport().builder()
                .targetType(TargetType.CERTAIN)
                .target(1L)
                .build();
        Alarm alarm = alarmService.alarmFromDto(dto, 1L);




    }

    @Test
    void testAllAlarmFromDto() {
        AlarmDto dto = getAlarmDtoSupport().builder()
                .targetType(TargetType.CERTAIN)
                .target(1L)
                .build();
        Alarm alarm = alarmService.alarmFromDto(dto);
    }
}