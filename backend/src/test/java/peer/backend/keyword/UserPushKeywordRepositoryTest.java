package peer.backend.keyword;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserPushKeyword;
import peer.backend.repository.user.UserPushKeywordRepository;
import peer.backend.repository.user.UserRepository;

@DisplayName("UserPushKeyword Repository 테스트")
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
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
            .password("test")
            .nickname("test")
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

    @Test
    @DisplayName("UserPushKeyword save 테스트")
    void saveTest() {
        User user = userRepository.findAll().get(0);
        System.out.println(user.getId());
        UserPushKeyword userPushKeyword = UserPushKeyword.builder()
            .user(user)
            .keyword("hello").build();
        UserPushKeyword saved = userPushKeywordRepository.save(userPushKeyword);
        System.out.println(saved.getKeyword());
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
        List<UserPushKeyword> findList = userPushKeywordRepository.findAllByUserId(user.getId());
        assertEquals(findList.size(), 1);
        userPushKeywordRepository.deleteByUserIdAndKeyword(user.getId(),
            userPushKeyword.getKeyword());
        findList = userPushKeywordRepository.findAllByUserId(user.getId());
        assertEquals(findList.size(), 0);
    }
}
