package peer.backend.service.messageOld;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import peer.backend.entity.messageOld.Message;
import peer.backend.entity.user.User;
import peer.backend.repository.messageOld.MessageRepository;
import peer.backend.repository.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Message Test")
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageServiceOld messageServiceOld;

    User user0, user1, user2, user3;

    @Test
    void setting() {
        user0 = User.builder()
            .password("password")
            .name("John")
            .email("john@example.com")
            .nickname("userzero")
            .isAlarm(true)
            .address("123 Main St")
            .certification(true)
            .company("ABC Inc.")
            .introduce("Hello, I'm John.")
            .peerLevel(2L)
            .representAchievement("Achievement XYZ")
            .build();

        user1 = User.builder()
            .password("password1")
            .name("User One")
            .email("user1@example.com")
            .nickname("userone")
            .isAlarm(true)
            .address("123 First St")
            .certification(true)
            .company("Company A")
            .introduce("Hello, I'm User One.")
            .peerLevel(1L)
            .representAchievement("Achievement ABC")
            .build();

        user2 = User.builder()
            .password("password2")
            .name("User Two")
            .email("user2@example.com")
            .nickname("usertwo")
            .isAlarm(true)
            .address("456 Second St")
            .certification(true)
            .company("Company B")
            .introduce("Hello, I'm User Two.")
            .peerLevel(2L)
            .representAchievement("Achievement DEF")
            .build();

        user3 = User.builder()
            .password("password3")
            .name("User Three")
            .email("user3@example.com")
            .nickname("userthree")
            .isAlarm(false)
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
