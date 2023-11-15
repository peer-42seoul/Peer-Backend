package peer.backend.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@EnableWebMvc
@Slf4j
@Component
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
        if (index.getUserIdx1().equals(target.getId()))
            msgNumber = index.getUnreadMessageNumber1();
        else
            msgNumber = index.getUnreadMessageNumber2();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");
        String formattedDateTime = conversation.getCreatedAt().format(formatter);

        MsgObjectDTO ret = new MsgObjectDTO();
        ret.setTargetId(target.getId());
        ret.setTargetNickname(target.getNickname());
        ret.setTargetProfile(target.getImageUrl());
        ret.setConversationId(index.getConversationId());
        if (index.getUserIdx1().equals(target.getId())) {
            ret.setUnreadMsgNumber(index.getUnreadMessageNumber2());
        }
        if (index.getUserIdx2().equals(target.getId())) {
            ret.setUnreadMsgNumber(index.getUnreadMessageNumber1());
        }
        ret.setLatestMsgId(conversation.getMsgId());
        ret.setLatestContent(conversation.getText());
        ret.setLatestDate(formattedDateTime);

        return ret;
    }

    public List<MessagePiece> executeNativeSQLQueryForMessagePiece(String sql) {
        Query query = entityManager.createNativeQuery(sql, MessagePiece.class);
        return query.getResultList();
    }

    public List<MessagePiece> executeNativeSQLQueryForMessagePiece(String sql, Map<String, Long> mapping) {
        Query query = entityManager.createNativeQuery(sql, MessagePiece.class);
        for (Map.Entry<String, Long> entry : mapping.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    public List<User> executeNativeSQLQueryForUser(String sql) {
        Query query = entityManager.createNativeQuery(sql, User.class);
        return query.getResultList();
    }

    public String makeFormattedDate(LocalDateTime value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");
        return value.format(formatter);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public List<Msg> makeMsgDataWithMessagePiece(List<MessagePiece> talks) {
        List<Msg> innerData = new ArrayList<>();
        User data = null;
        Optional<User> rawData;
        long size = 0;
        boolean isEnd = false;

        for (MessagePiece piece : talks) {
            rawData = this.userRepository.findById(piece.getSenderId());
            if (rawData.isEmpty())
                continue;
            data = rawData.get();

            if (talks.size() > 20) {
                if (size == 20)
                    break ;
            }
            else {
                if (size + 1 == talks.size())
                    isEnd = true;
                else if (size > talks.size()) {
                    break ;
                }
            }
            Msg talkBubble =  Msg.builder().
                    userId(piece.getSenderId()).
                    msgId(piece.getMsgId()).
                    content(piece.getText()).
                    date(this.makeFormattedDate(piece.getCreatedAt())).
                    end(isEnd).build();
            // piece update
            if (piece.getReadAt() == null) {
                piece.setReadAt(LocalDateTime.now());
                this.pieceRepository.save(piece);
            }

            innerData.add(talkBubble);
            isEnd = false;
            size++;
        }
        return innerData;
    }

    public MsgListDTO makeMsgDTO(User owner, User targetUser, List<Msg>innerData)
    {
        MsgListDTO ret = new MsgListDTO();
        MsgOwner user = MsgOwner.builder().
                userId(owner.getId()).
                userNickname(owner.getNickname()).
                userProfile(owner.getImageUrl()).build();
        MsgTarget msgTarget = MsgTarget.builder().
                userId(targetUser.getId()).
                userNickname(targetUser.getNickname()).
                userProfile(targetUser.getImageUrl()).build();

        ret.setMsgOwner(user);
        ret.setMsgTarget(msgTarget);
        ret.setMsgList(innerData);
        return ret;
    }

    @Transactional
    public void recoveryMessageIndex(MessageIndex targetIndex, long who) {
        if (who == 1) {
            targetIndex.setUser1delete(false);
        } else {
            targetIndex.setUser2delete(false);
        }
        this.indexRepository.save(targetIndex);
    }

    @Transactional
    public void checkMessageIndexExistOrNot(long ownId, long userId) throws Exception {
        Optional<MessageIndex> rawIndex = this.indexRepository.findByUserIdx(ownId, userId);
        if (rawIndex.isEmpty())
            return;
        MessageIndex index = rawIndex.orElseThrow(() -> new Exception("Database Error is happened."));
        if (index.getUserIdx1().equals(ownId)) {
            if (index.isUser1delete() || index.isUser2delete()) {
//                this.recoveryMessageIndex(index, 1);
                return;
            }
        } else if (index.getUserIdx2().equals(ownId)) {
            if (index.isUser2delete() || index.isUser1delete()) {
//                this.recoveryMessageIndex(index, 2);
                return;
            }
        }
        throw new DataIntegrityViolationException("There is already message");
    }

    @Transactional(readOnly = false)
    public MessageIndex saveNewData(User owner, User target) {
        MessageIndex newData = MessageIndex.builder().
                userIdx1(owner.getId()).
                userIdx2(target.getId()).
                unreadMessageNumber1(0L).
                unreadMessageNumber2(0L).
                user1(owner).
                user2(target).
                build();
        return this.indexRepository.save(newData);
    }
}
