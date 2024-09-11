package peer.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import peer.backend.entity.chat.Chat;
import peer.backend.repository.ChatRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    @Transactional
    public Chat createChat(Long userId, String userName, Long teamId, String message,
        LocalDateTime date) {
        return this.chatRepository.save(new Chat(userId, userName, teamId, message, date));
    }

    @Transactional
    public List<Chat> getChatList(Long teamId) {
        return this.chatRepository.findAllByTeamId((teamId));
    }
}
