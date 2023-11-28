package peer.backend.repository.alarm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import peer.backend.entity.alarm.Alarm;
import peer.backend.entity.alarm.enums.Priority;
import peer.backend.entity.alarm.enums.TargetType;
import peer.backend.entity.user.User;
import peer.backend.repository.team.TeamRepository;
import peer.backend.repository.team.TeamUserRepository;
import peer.backend.repository.user.UserRepository;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.transaction.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("알림 저장소 테스트")
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
class AlarmRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AlarmRepository alarmRepository;
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
    }


    @DisplayName("알림 저장")
    @Test
    void save() {
        Alarm alarm = Alarm.builder()
                .id(1L)
                .title("test")
                .message("message")
                .targetType(TargetType.ALL)
                .link("link")
                .sent(false)
                .priority(Priority.SCHEDULED)
                .scheduledTime(new Date())
                .build();
        alarmRepository.save(alarm);
    }
    @DisplayName("알림에 속상 대상 찾기")
    @Test
    void findByTarget() {

    }
}