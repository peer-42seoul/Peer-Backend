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

@DisplayName("Message Service Test")
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageServiceTest {

    @Mock
    private MessageIndexRepository indexRepository;
    @Mock
    private MessagePieceRepository pieceRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageMainService mainService;
    @InjectMocks
    private MessageSubService subService;

    User user0, user1, user2, user3;

    @BeforeEach
    public void setUp() {
        //TODO: 어떻게 설정하지?
    }

    @Test
    public void testAct1() {
        //TODO: 뭘 할지 정할 것
    }

}
