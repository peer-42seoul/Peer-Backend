package peer.backend.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import peer.backend.dto.message.MsgObjectDTO;
import peer.backend.dto.message.TargetDTO;
import peer.backend.dto.security.Message;
import peer.backend.entity.message.MessageIndex;
import peer.backend.entity.user.User;
import peer.backend.repository.message.MessageIndexRepository;
import peer.backend.repository.message.MessagePieceRepository;
import peer.backend.repository.user.UserRepository;

import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@EnableWebMvc
public class MessageService {
    private final UserRepository userRepository;
    private final MessageIndexRepository messageIndexRepository;
    private final MessagePieceRepository messagePieceRepository;

    @Async
    @Transactional(readOnly = true)
    public List<MsgObjectDTO> getLetterListByUserId(long userId) {
        // TODO: 사용자 계정 탐색
        // TODO: 대화 탐색
        // TODO: conversationId를 활용하여 latest 컨텐츠를 가져온다

        Optional<User> target = userRepository.findById(userId);
        if (target == null) {

        }
        Optional<List<MessageIndex>> values = messageIndexRepository.findByUserId(userId);
        if (values == null) {
            //TODO: error handling
        }
        List<MessageIndex> list = values.orElseGet(() -> null);
        list.get(0).getConversationId().longValue();
        List<MsgObjectDTO> ret = null;
        return ret;
    }

    @Async
    @Transactional(readOnly = true)
    public long deleteLetterList(long userId, List<TargetDTO> list){

        return 1;
    }
}
