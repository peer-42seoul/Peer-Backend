package peer.backend.controller.message;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.entity.message.Message;
import peer.backend.dto.message.MessageSendRequest;
import peer.backend.entity.user.User;
import peer.backend.repository.message.MessageRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.message.MessageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/message")
public class MessageController {

    private final MessageService messageService;

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @GetMapping("/setting")
    public void setting()
    {
        User user0 = User.builder()
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

        User user1 = User.builder()
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

        User user2 = User.builder()
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

        User user3 = User.builder()
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

        userRepository.save(user0);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

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

        messageRepository.save(message1);
        messageRepository.save(message2);
        messageRepository.save(message3);
        messageRepository.save(message4);
        messageRepository.save(message5);
        messageRepository.save(message6);
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity userMessageList(@PathVariable("userId") Long userId)
    {
        return new ResponseEntity(messageService.myMessageList(userId), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity messageDetail(@PathVariable("userId") Long userId)
    {
        return new ResponseEntity(messageService.userDetailMessage(userId), HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity sendMessage(@PathVariable("userId") Long userId,
        @RequestBody MessageSendRequest messageSendRequest) {
        return new ResponseEntity(messageService.sendMessage(userId, messageSendRequest), HttpStatus.OK);
    }
}
