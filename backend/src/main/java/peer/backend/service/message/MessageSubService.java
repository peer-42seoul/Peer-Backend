package peer.backend.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import peer.backend.dto.message.*;
import peer.backend.entity.message.MessageIndex;
import peer.backend.entity.message.MessagePiece;
import peer.backend.entity.user.User;
import peer.backend.repository.message.MessageIndexRepository;
import peer.backend.repository.message.MessagePieceRepository;
import peer.backend.repository.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.swing.text.html.parser.Entity;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private EntityManager entityManager;

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

    /**
     * OutLine : DB 상에 저장된 대화 목록 Index의 List 를 반환한다.
     * @param userId
     * @return try - catch 문을 활용해서 받거나 아니면 Exception을 발생시킬 수 있다.
     */
    @Transactional(readOnly = true)
    public List<MessageIndex> getMessageIndexList(long userId) {
        Optional<List<MessageIndex>> listData = this.indexRepository.findByUserId(userId);
        List<MessageIndex> retData = null;
        return retData = listData.orElseThrow(() -> new NoSuchElementException("There are no messges"));
    }

    /**
     * OutLine : MsgObjectDTO 를 생성하는 메서드
     * @param index
     * @param target
     * @param conversation
     * @return
     */
    public MsgObjectDTO makeMsgObjectDTO(MessageIndex index, User target, MessagePiece conversation) {
        long msgNumber;
        if (index.getUserIdx1() == target.getId())
            msgNumber = index.getUnreadMessageNumber1();
        else
            msgNumber = index.getUnreadMessageNumber2();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");
        String formattedDateTime = conversation.getCreatedAt().format(formatter);

        MsgObjectDTO ret = new MsgObjectDTO();
        ret.builder().targetId(target.getId()).
            targetNickname(target.getNickname()).
                targetProfile(target.getImageUrl()).
                conversationId(index.getConversationId()).
                unreadMsgNumber(msgNumber).
                msgId(conversation.getMsgId()).
                latestContent(conversation.getText()).
                latestDate(formattedDateTime);

        return ret;
    }

    public List<MessagePiece> executeNativeSQLQueryForMessagePiece(String sql) {
        Query query = entityManager.createNativeQuery(sql, MessagePiece.class);
        List<MessagePiece> result = query.getResultList();
        return result;
    }

}
