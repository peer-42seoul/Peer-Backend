package peer.backend.service.message;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import peer.backend.dto.message.MessageUserDTO;
import peer.backend.entity.message.Message;
import peer.backend.entity.user.User;
import peer.backend.repository.message.MessageRepository;
import peer.backend.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Message Test")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    User user0, user1, user2, user3;
    @Test
    void setting() {
        user0 = User.builder()
            .userId("userId123")
            .password("password")
            .name("John")
            .email("john@example.com")
            .nickname("userzero")
            .birthday(LocalDate.of(1990, 1, 1))
            .isAlarm(true)
            .phone("123-456-7890")
            .address("123 Main St")
            .certification(true)
            .company("ABC Inc.")
            .introduce("Hello, I'm John.")
            .peerLevel(2L)
            .representAchievement("Achievement XYZ")
            .build();

        user1 = User.builder()
            .userId("user1")
            .password("password1")
            .name("User One")
            .email("user1@example.com")
            .nickname("userone")
            .birthday(LocalDate.of(1990, 1, 1))
            .isAlarm(true)
            .phone("111-111-1111")
            .address("123 First St")
            .certification(true)
            .company("Company A")
            .introduce("Hello, I'm User One.")
            .peerLevel(1L)
            .representAchievement("Achievement ABC")
            .build();

        user2 = User.builder()
            .userId("user2")
            .password("password2")
            .name("User Two")
            .email("user2@example.com")
            .nickname("usertwo")
            .birthday(LocalDate.of(1995, 3, 15))
            .isAlarm(true)
            .phone("222-222-2222")
            .address("456 Second St")
            .certification(true)
            .company("Company B")
            .introduce("Hello, I'm User Two.")
            .peerLevel(2L)
            .representAchievement("Achievement DEF")
            .build();

        user3 = User.builder()
            .userId("user3")
            .password("password3")
            .name("User Three")
            .email("user3@example.com")
            .nickname("userthree")
            .birthday(LocalDate.of(1985, 8, 20))
            .isAlarm(false)
            .phone("333-333-3333")
            .address("789 Third St")
            .certification(false)
            .company("Company C")
            .introduce("Hello, I'm User Three.")
            .peerLevel(3L)
            .representAchievement("Achievement GHI")
            .build();

        Message message1 = Message.builder()
            .content("예시1")
            .sender(user0)
            .receiver(user1)
            .build();

        Message message2 = Message.builder()
            .content("예시2")
            .sender(user0)
            .receiver(user1)
            .build();

        Message message3 = Message.builder()
            .content("예시3")
            .sender(user1)
            .receiver(user2)
            .build();

        Message message4 = Message.builder()
            .content("예시4")
            .sender(user1)
            .receiver(user2)
            .build();

        Message message5 = Message.builder()
            .content("예시5")
            .sender(user2)
            .receiver(user1)
            .build();

        Message message6 = Message.builder()
            .content("예시6")
            .sender(user2)
            .receiver(user0)
            .build();

    }

//    @Test
//    void myMessageUserListTest() {
//        List<MessageUserDTO> messageUserDTOS = messageService.myMessageList(user0.getId());
//        for (MessageUserDTO dto : messageUserDTOS)
//        {
//            System.out.println("dto.getProfileImage() = " + dto.getProfileImage());
//            System.out.println("dto.getNickName() = " + dto.getNickName());
//        }
//        assertThat(0).isEqualTo(0);
//    }
}