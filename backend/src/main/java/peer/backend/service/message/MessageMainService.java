package peer.backend.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import peer.backend.comparator.MessagePieceComparator;
import peer.backend.dto.asyncresult.AsyncResult;
import peer.backend.dto.message.*;
import peer.backend.entity.message.MessageIndex;
import peer.backend.entity.message.MessagePiece;
import peer.backend.entity.user.User;
import peer.backend.repository.message.MessageIndexRepository;
import peer.backend.repository.message.MessagePieceRepository;
import peer.backend.repository.user.UserRepository;

import javax.management.Query;
import javax.persistence.EntityManager;
import javax.swing.text.html.Option;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@EnableWebMvc
public class MessageMainService {
    private final UserRepository userRepository;
    private final MessageIndexRepository indexRepository;
    private final MessagePieceRepository pieceRepository;
    private final MessageSubService subService;


    /**
     * OutLine : 사용자 대화 목록을 모두 발견하고, 해당 목록과 마지막 대화를 불러와 MsgObject를 만들어 전달한다.
     * Logic :
     * 1. 사용자를 파악한다.
     * 2. MessageIndex를 전부 추려서 ListUp 한다.
     * 3. List<MessageIndex>를 사용하여 순회하면서 MsgObjectDTO 를 채우고 이를 추가한다.(conversationId, unreadMsgNumber)
     * 3-1. Index를 통해 target 대상의 정보도 발견하고 받아낸다.(targetId, Nickname, profile URL)
     * 3-2. MessagePiece에서 ConversationId를 가지고 latestContent를 확인하고, 전달 일자를 conversion해서 추가한다.(msgId, latestContent(Text), latestSendDate)
     * 3-3. 최종적으로 데이터를 MsgObjectDTO로 반환 된 것을 받아서 추가해준다.
     * @param userId
     * @return
     */
    @Async
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<AsyncResult<List<MsgObjectDTO>>> getLetterListByUserId(long userId) {
        Optional<User> msgOwnerData = userRepository.findById(userId);
        User msgOwner = new User();
        try {
            // Owner Get
            msgOwner = msgOwnerData.orElseThrow(() -> new NoSuchElementException("User Not found"));
        } catch (Exception e) {
            return CompletableFuture.completedFuture((AsyncResult.failure(e)));
        }

        List<MessageIndex> msgList = null;
        try {
            // msgIndex Get
            msgList = this.subService.getMessageIndexList(msgOwner.getId());
        } catch (NoSuchElementException e) {
            return CompletableFuture.completedFuture((AsyncResult.failure(e)));
        }

        List<MsgObjectDTO> retList = null;
        User target = null;

        // Index 기준으로 반복문으로 MsgObject 작성 시작
        for (MessageIndex msg : msgList) {
            // 대화
            MessagePiece conversation= this.pieceRepository.findTopByConversationId(msg.getConversationId()).orElseGet(() -> null);
            // 상대방 확인
            if (msg.getUserIdx1() == msgOwner.getId()) {
                if (msg.isUser1delete())
                    continue;
                target = this.userRepository.findById(msg.getUserIdx2()).get();
            } else {
                if (msg.isUser2delete())
                    continue;
                target = this.userRepository.findById(msg.getUserIdx1()).get();
            }

            retList.add(this.subService.makeMsgObjectDTO(msg, target, conversation));
        }
        return CompletableFuture.completedFuture(AsyncResult.success(retList));
    }

    /**
     * OutLine : Letter 목록을 전달 받으면 대화목록을 삭제 한다.
     * Logic :
     * 1. 삭제 리스트를 하나씩 순회한다.
     * 2. 데이터 삭제를 했다고 index에 표시한다.
     * 2-1. 양쪽 다 삭제 처리가 true가 된 index에 대해서는 DB에서 삭제를 진행한다.
     * 3. 개수를 반환한다.
     * 4. DB 상 에러 발생 시 에러 처리를 진행 한다.
     * @param userId
     * @param list
     * @return
     */
    @Async
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public CompletableFuture<AsyncResult<Long>> deleteLetterList(long userId, List<TargetDTO> list){
        //TODO: Make Logic
        Long ret;
        ret = 0L;

        List<MessageIndex> targetsData = this.indexRepository.findByUserId(userId).orElseGet(() -> null);
        if (targetsData == null)
            return CompletableFuture.completedFuture(AsyncResult.success(0L));
        boolean check = false;
        for (TargetDTO target : list) {
            for (MessageIndex data : targetsData) {
                if (data.getUserIdx1() == target.getTargetId()) {
                    data.setUser1delete(true);
                    check = true;
                }
                if (data.getUserIdx2() == target.getTargetId()) {
                    data.setUser2delete(true);
                    check = true;
                }
                if (check) {
                    check = false;
                    if (data.isUser1delete() && data.isUser2delete()) {
                        this.indexRepository.delete(data);
                        ret++;
                        targetsData.remove(data);
                        break ;
                    }
                    else {
                        this.indexRepository.save(data);
                        ret++;
                        targetsData.remove(data);
                        break ;
                    }
                    // TODO: check CASCADE so you need to check is MessagePieces deleted or not
                }
            }
        }

        return CompletableFuture.completedFuture(AsyncResult.success(ret));
    }

    /**
     * OutLine : String keyword 를 전달하고 유사성 높은 대상을 간추려 낸다.
     * Logic:
     * 1. keyword 를 받는다.
     * 2. UserRepository 를 활용해서 데이터를 간추려낸다.
     * 3. 에러 핸들링 이후 DTO 에 담아 전달한다.
     * @param keyword
     * @return
     */
    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<AsyncResult<List<LetterTargetDTO>>> findUserListByUserNickname(String keyword) {
        List<User> raw = this.userRepository.findByKeyWord(keyword).orElseGet(() -> null);
        if (raw == null)
            return CompletableFuture.completedFuture(AsyncResult.success(null));
        List<LetterTargetDTO> ret = null;
        for (User candidate: raw) {
            LetterTargetDTO data = new LetterTargetDTO();
            try {
                data.builder().
                        targetId(candidate.getId()).
                        targetNickname(candidate.getNickname()).
                        targetProfile(candidate.getImageUrl());
                ret.add(data);
            } catch (Exception e) {
                //TODO: error handling
            }
        }
        return CompletableFuture.completedFuture(AsyncResult.success(ret));
    }

    /**
     * OutLine : 새로운 대화를 생성하고 저장합니다.
     * Logic :
     * 1. 사용자와 대상에 대한 MessageIndex 생성하기
     * 2. DB에 초기 값 저장
     * 3. 정상처리 여부 확인(데이터 저장 여부)
     * 3-1. 데이터 조건을 통해 데이터 저장 여부 확인
     * 3-2. getMessageIndex 로 index 객체 반환 받기
     * 4. sendMessage 메소드 실행하기
     * @param userId : 사용자의 userID
     * @param message : 대상이 되는 대상에게 메시지를 보내는 경우
     * @return : 성공 여부에 따라 messageIndex 객체를 반환 받는다.
     */
    @Async
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<AsyncResult<MessageIndex>> makeNewMessageIndex(long userId, MsgContentDTO message) {
        User owner;
        User target;
        Optional<User> data = this.userRepository.findById(message.getTargetId());
        try {
            target = data.orElseThrow(() -> new Exception("User not found"));
        } catch (Exception e) {
            return CompletableFuture.completedFuture((AsyncResult.failure(e)));
        }
        Optional<User> dataUser = this.userRepository.findById(userId);
        try {
            owner = dataUser.orElseThrow(() -> new Exception("User not found"));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(AsyncResult.failure(e));
        }

        MessageIndex newData = new MessageIndex();
        newData.setUserIdx1(owner.getId());
        newData.setUserIdx2(target.getId());
        newData.setUnreadMessageNumber1(0L);
        newData.setUnreadMessageNumber2(0L);
        newData.setUser1delete(false);
        newData.setUser2delete(false);
        newData.setUser1(owner);
        newData.setUser2(target);

        MessageIndex saved;
        try {
            saved = this.indexRepository.save(newData);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(AsyncResult.failure(e));
        }
        return CompletableFuture.completedFuture(AsyncResult.success(saved));
    }

    /**
     * OutLine : 전달 받은 대화 내용을 저장합니다.
     * Logic :
     * 1. conversationId 를 활용하여 새로운 Message 를 생성한다.
     * 2. 해당 메시지를 저장한다.
     * 3. 해당 메시지의 index의 값도 성공적으로 저장한다.
     * 3. 성공적인 저장 여부를 판단하고 반환한다.
     * @param index : 메시지 index 객체입니다.
     * @param userId
     * @param message
     * @return true 면 정상 저장. 만약 실패하면 false 를 반환한다.
     */

    @Transactional(readOnly = false)
    public boolean sendMessage(MessageIndex index, long userId, MsgContentDTO message) {
        User user1 = index.getUser1();
        User user2 = index.getUser2();
        User msgOwner = null;

        msgOwner = user1.getId() == userId ? user1 : user2;

        MessagePiece letter = new MessagePiece();
        letter.builder().
                conversationId(index.getConversationId()).
                senderNickname(msgOwner.getNickname()).
                senderId(msgOwner.getId()).
                text(message.getContent());

        try {
            this.pieceRepository.save(letter);
        } catch (OptimisticLockingFailureException e) {
            return false;
        }

        if (msgOwner.getId() == userId)
        {
            Long unread = index.getUnreadMessageNumber1();
            unread += 1;
            index.setUnreadMessageNumber1(unread);
        }
        else
        {
            Long unread = index.getUnreadMessageNumber2();
            unread += 1;
            index.setUnreadMessageNumber2(unread);
        }

        try {
            this.indexRepository.save(index);
        } catch (OptimisticLockingFailureException e) {
            return false;
        }

        return true;
    }

    /**
     * OutLine : userId 와 구체적인 메시지 DTO를 통해 쪽지들을 불러온다.
     * Logic :
     * 0. msgIndex에서 해당 유저가 이미 삭제 처리를 한 상태인지 체크한다(했으면 반환하지 않는다)
     * 1. targetId, Conversation Id를 통해 쪽지 데이터를 전체 들고옴.
     * 2. 데이터를 MsgDTO 에 맞춰 가공 처리한다.
     * @param userId
     * @param target
     * @return
     */
    @Async
    @Transactional(readOnly = true)
    public List<MsgDTO> getSpecificLetterListByUserIdAndTargetId(long userId, SpecificMsgDTO target) {

        MessageIndex targetIndex = this.indexRepository.findTopByConversationId(target.getConversationalId()).orElseGet(() -> null);
        if (targetIndex == null)
            return null;
        if (targetIndex.getUserIdx1() == userId) {
            if (targetIndex.isUser1delete())
                return null;
        } else if (targetIndex.getUserIdx2() == userId) {
            if (targetIndex.isUser2delete())
                return null;
        }
        List<MessagePiece> talks = this.subService.executeNativeSQLQueryForMessagePiece("SELECT * FROM message_piece WHERE conversationId = :" + target.getConversationalId() + "ORDER BY createdAt DESC LIMIT 21");
        Collections.sort(talks, new MessagePieceComparator());

        List<MsgDTO> ret = new ArrayList<>();
        User data = null;
        Optional<User> rawData;
        long size = 0;
        boolean isEnd = false;
        for (MessagePiece piece : talks) {
            rawData = this.userRepository.findById(piece.getSenderId());
            data = rawData.get();

            if (talks.size() > 20) {
                if (size < 18) isEnd = false;
                else if (size == 19) isEnd = false;
                else if (size == 20)
                    break ;
            }
            else {
                // TODO: 여기 조건 정확히 넣어줘야 함. talks size가 작은 경우
                if (size + 1 < talks.size())
                    isEnd = false;
                else if (size + 1 == talks.size())
                    isEnd = true;
                else if (size > talks.size()) {
                    break ;
                }
            }
            MsgDTO talkBubble = new MsgDTO();
            talkBubble.builder().
                    senderId(piece.getSenderId()).
                    senderNickname(piece.getSenderNickname()).
                    targetProfile(data.getImageUrl()).
                    msgId(piece.getMsgId()).
                    content(piece.getText()).
                    isEnd(isEnd).build();
            //TODO make new MsgDTO;
            ret.add(talkBubble);
            isEnd = false;
            size++;
        }
        return ret;
    }

    @Async
    @Transactional(readOnly = true)
    public List<MsgDTO> getSpecificLetterUpByUserIdAndTargetId(long userId, long page, SpecificScrollMsgDTO target) {
        //TODO: Make Logic
        List<MsgDTO> ret = null;
        return ret;
    }
}