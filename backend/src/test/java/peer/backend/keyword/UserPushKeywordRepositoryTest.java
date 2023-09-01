package peer.backend.keyword;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserPushKeyword;
import peer.backend.repository.user.UserPushKeywordRepository;
import peer.backend.repository.user.UserRepository;

@DisplayName("UserPushKeyword Repository 테스트")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserPushKeywordRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPushKeywordRepository userPushKeywordRepository;

    @BeforeEach
    void beforeEach() {
        User user = User.builder()
            .name("test")
            .email("test@test.com")
            .nickname("test")
            .birthday(LocalDate.now())
            .isAlarm(false)
            .phone("test")
            .address("test")
            .certification(false)
            .company("test")
            .introduce("test")
            .peerLevel(0L)
            .representAchievement("test")
            .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("BeforeEach에서 유저가 잘 insert 되었는지 테스트")
    void beforeEachTest() {
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("UserPushKeyword save 테스트")
    void saveTest() {
        User user = this.userRepository.findAll().get(0);
        UserPushKeyword userPushKeyword = UserPushKeyword.builder()
            .user(user)
            .keyword("hello").build();
        userPushKeywordRepository.save(userPushKeyword);
        assertThat(userPushKeywordRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("UserPushKeyword findAllByUserId 테스트")
    void findTest() {
        User user = userRepository.findAll().get(0);
        UserPushKeyword userPushKeyword = UserPushKeyword.builder()
            .user(user)
            .keyword("hello").build();
        userPushKeywordRepository.save(userPushKeyword);
        List<UserPushKeyword> findList = userPushKeywordRepository.findAllByUserId(user.getId());
        assertEquals(findList.size(), 1);
        assertEquals(findList.get(0).getKeyword(), "hello");
    }

    @Test
    @DisplayName("UserPushKeyword deleteByUserIdAndKeyword 테스트")
    void deleteTest() {
        User user = this.userRepository.findAll().get(0);
        UserPushKeyword userPushKeyword = UserPushKeyword.builder()
            .user(user)
            .keyword("hello").build();
        userPushKeywordRepository.save(userPushKeyword);
        userPushKeywordRepository.deleteByUserIdAndKeyword(user.getId(),
            userPushKeyword.getKeyword());
        List<UserPushKeyword> findList = userPushKeywordRepository.findAllByUserId(user.getId());
        assertEquals(findList.size(), 0);
    }
}
