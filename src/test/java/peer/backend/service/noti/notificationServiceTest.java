package peer.backend.service.noti;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import peer.backend.dto.noti.old.AlarmDto;
import peer.backend.entity.noti.old.Notification;
import peer.backend.entity.noti.old.enums.Priority;
import peer.backend.entity.noti.old.enums.TargetType;
import peer.backend.service.noti.old.NotificationServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamService Test")
class notificationServiceTest {

    @InjectMocks
    private NotificationServiceImpl alarmService;

    AlarmDto getAlarmDtoSupport() {
        return AlarmDto.builder()
                .title("test")
                .link("test")
                .message("test")
                .priority(Priority.SCHEDULED)
                .build();
    }

    @Test
    void testCertainAlarmFromDto() {
        AlarmDto dto = getAlarmDtoSupport().builder()
                .targetType(TargetType.CERTAIN)
                .build();
        Notification alarm = alarmService.alarmFromDto(dto);




    }

}