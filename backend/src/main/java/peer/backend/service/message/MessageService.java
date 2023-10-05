package peer.backend.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import peer.backend.repository.message.MessageIndexRepository;
import peer.backend.repository.message.MessagePieceRepository;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@EnableWebMvc
public class MessageService {
    private final UserRepository userRepository;
    private final MessageIndexRepository messageIndexRepository;
    private final MessagePieceRepository messagePieceRepository;


}
