package peer.backend.controller.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import peer.backend.dto.user.UserPasswordRequest;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.user.UserService;

@SpringBootTest
class userControllerTest {

    @Autowired
    UserService userService;
    @Autowired
    UserController userController;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setup() {
        User user1 = User.builder()
            .id(1L)
            .user_id("cjswl1357")
            .password("123456")
            .name("이용훈")
            .email("cjswl1357@ncnc.com")
            .nickname("닉네임111")
            .birthday(LocalDateTime.now())
            .is_alarm(true)
            .phone("010-8331-2849")
            .address("성남시")
            .certification(true)
            .company("굳")
            .introduce("나는 이용훈")
            .peerLevel(10L)
            .representAchievement("냠냠")
            .build();

        User user2 = User.builder()
            .id(2L)
            .user_id("haidi1357")
            .password("12421421")
            .name("이용훈22")
            .email("haidi1357@ncnc.com")
            .nickname("닉네임2222")
            .birthday(LocalDateTime.now())
            .is_alarm(true)
            .phone("010-2314-4921")
            .address("대구시")
            .certification(false)
            .company("341굳")
            .introduce("나는 이용훈22")
            .peerLevel(102L)
            .representAchievement("냠냠2313")
            .build();
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    public void 유저프로필조회_09() {

        User compareUser = userRepository.findById(1L).orElseThrow(() ->
        {
            return new NotFoundException("유저가 존재하지 않아요");
        });
        assertThat(compareUser.getName()).isEqualTo("이용훈");
    }

//    @Test
//    public void 유저비밀번호변경() {
//        UserPasswordRequest userPasswordRequest = new UserPasswordRequest("123456","123456789");
//        User updatedUser = userRepository.findById(1L).orElse(null);
//        assertThat(updatedUser).isNotNull();
//        assertThat(updatedUser.getPassword()).isEqualTo("123456789");
//    }

    @Test
    public void 유저전화번호변경() throws Exception {
        String newPhone = "010-1234-5678";

        userService.editPhone(1L, newPhone);

        User updatedUser = userRepository.findById(1L).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getPhone()).isEqualTo(newPhone);
    }
}