package peer.backend.repository.noti;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import peer.backend.entity.noti.Notification;
import peer.backend.entity.noti.enums.Priority;
import peer.backend.entity.noti.enums.TargetType;
import peer.backend.entity.user.User;
import peer.backend.repository.user.UserRepository;

import javax.transaction.Transactional;

import java.util.Date;

@DisplayName("알림 저장소 테스트")
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
class notificationRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationRepository notificationRepository;
    @BeforeEach
    void beforeEach() {
        User user = User.builder()
                .name("test")
                .email("test@test.com")
                .nickname("test")
                .password("test")
                .isAlarm(false)
                .address("test")
                .certification(false)
                .company("test")
                .introduce("test")
                .peerLevel(0L)
                .representAchievement("test")
                .build();
        userRepository.save(user);
        save();
    }

    @DisplayName("전체 알림 생성")
    Notification crateAlarmAll() {
        return Notification.builder()
                .id(1L)
                .title("test")
                .message("message")
                .target(0L)
                .targetType(TargetType.ALL)
                .link("link")
                .sent(false)
                .priority(Priority.SCHEDULED)
                .scheduledTime(new Date())
                .build();
    }
    @DisplayName("특정 대상 알림 생성")
    Notification createAlarmCertain() {
        return Notification.builder()
                .id(1L)
                .title("test")
                .message("message")
                .target(1L)
                .targetType(TargetType.CERTAIN)
                .link("link")
                .sent(false)
                .priority(Priority.SCHEDULED)
                .scheduledTime(new Date())
                .build();
    }

    @DisplayName("알림 저장")
    void save() {
//        Alarm alarm = create(); // using create suffix certain or all
//        alarmRepository.save(alarm);
    }
//    @DisplayName("알림 찾기")
//    @Test
//    void findById() {
//        // given
//        alarmRepository.save(createAlarmCertain());
//        Alarm alarm1 = alarmRepository.getReferenceById(1L);
//        Alarm alarm2 = alarmRepository.findByUserIdAndAlarmType(1L).get(0);
//        // when
//        assertEquals(alarm1, alarm2);
//    }
}