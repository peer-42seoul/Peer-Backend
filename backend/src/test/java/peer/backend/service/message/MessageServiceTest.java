package peer.backend.service.message;

import org.hibernate.service.spi.InjectService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.entity.user.User;
import peer.backend.repository.message.MessageIndexRepository;
import peer.backend.repository.message.MessagePieceRepository;
import peer.backend.repository.messageOld.MessageRepository;
import peer.backend.repository.user.UserRepository;

import static org.mockito.Mockito.when;

@DisplayName("Message Service Test")
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageServiceTest {

    @InjectMocks
    private MessageMainService mainService;

    @Mock
    private MessageIndexRepository indexRepository;
    @Mock
    private MessagePieceRepository pieceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageSubService subService;

    User user0, user1, user2, user3;

    @BeforeEach
    public void setUp() {
        //TODO: 어떻게 설정하지?
        // User0 ~ User3 만들기
        // User0-1 대화 만들기 -> 메시지 0 넣기
        // User1-2 대화 만들기 -> 메시지 2 넣기
        // User0-3 대화 만들기 -> 메시지 3 넣기
        // User2-3 대화 만들기 -> 메시지 3 넣기
    }

    @Test
    public void testAct1()
    {
    }

}
