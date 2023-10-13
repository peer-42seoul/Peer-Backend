package peer.backend.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import peer.backend.dto.message.*;
import peer.backend.entity.message.MessageIndex;
import peer.backend.entity.user.User;
import peer.backend.repository.message.MessageIndexRepository;
import peer.backend.repository.message.MessagePieceRepository;
import peer.backend.repository.user.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@EnableWebMvc
public class MessageSubService {
    private final UserRepository userRepository;
    private final MessageIndexRepository indexRepository;
    private final MessagePieceRepository pieceRepository;

    /**
     * DB 상에 저장된 대화 목록의 index 객체를 반환합니다.
     * @param userId
     * @param targetId
     * @return try - catch 문을 활용해서 index 객체를 반환 받거나 Exception을 발생시킬 수 있습니다.
     */
    @Transactional(readOnly = true)
    public MessageIndex getMessageIndex(long userId, long targetId) {
        Optional<MessageIndex> data = this.indexRepository.findByUserIdx(userId, targetId);
        return data.orElseThrow(()-> new NoSuchElementException("Message not found"));
    }
}
