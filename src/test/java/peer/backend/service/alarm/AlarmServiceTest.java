package peer.backend.service.alarm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import peer.backend.dto.alarm.AlarmDto;
import peer.backend.entity.alarm.Alarm;
import peer.backend.entity.alarm.enums.Priority;
import peer.backend.entity.alarm.enums.TargetType;
import peer.backend.repository.alarm.AlarmRepository;
import peer.backend.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeamService Test")
class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AlarmServiceImpl alarmService;

//    @BeforeEach
//    void setup() {
//        saveUser();
//    }

//    void saveUser() {
//        User user = User.builder()
//                .name("test")
//                .email("test@test.com")
//                .nickname("test")
//                .password("test")
//                .isAlarm(false)
//                .address("test")
//                .certification(false)
//                .company("test")
//                .introduce("test")
//                .peerLevel(0L)
//                .representAchievement("test")
//                .build();
//        userRepository.save(user);
//    }
//
//    void saveAlarm(Alarm alarm) {
//        alarmRepository.save(alarm);
//
//    }

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
    void test() {
    }

    @Test
    void testCertainAlarmFromDto() {
        AlarmDto dto = getAlarmDtoSupport().builder()
                .targetType(TargetType.CERTAIN)
                .target(1L)
                .build();
        Alarm alarm = alarmService.AlarmFromDto(dto, 1L);




    }

    @Test
    void testAllAlarmFromDto() {
        AlarmDto dto = getAlarmDtoSupport().builder()
                .targetType(TargetType.CERTAIN)
                .target(1L)
                .build();
        Alarm alarm = alarmService.AlarmFromDto(dto);
    }
}