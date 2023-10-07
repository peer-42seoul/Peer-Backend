package peer.backend.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import peer.backend.dto.asyncresult.AsyncResult;
import peer.backend.dto.message.*;
import peer.backend.dto.security.Message;
import peer.backend.entity.message.MessageIndex;
import peer.backend.entity.message.MessagePiece;
import peer.backend.entity.user.User;
import peer.backend.repository.message.MessageIndexRepository;
import peer.backend.repository.message.MessagePieceRepository;
import peer.backend.repository.user.UserRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
@EnableWebMvc
public class MessageMainService {
    private final UserRepository userRepository;
    private final MessageIndexRepository indexRepository;
    private final MessagePieceRepository pieceRepository;

    @Async
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<AsyncResult<List<MsgObjectDTO>>> getLetterListByUserId(long userId) {
        // TODO: 사용자 계정 탐색
        // TODO: 대화 탐색
        // TODO: conversationId를 활용하여 latest 컨텐츠를 가져온다

        Optional<User> target = userRepository.findById(userId);
        if (target == null) {

        }
        Optional<List<MessageIndex>> values = indexRepository.findByUserId(userId);
        if (values == null) {
            //TODO: error handling
        }
        List<MessageIndex> list = values.orElseGet(() -> null);
        list.get(0).getConversationId().longValue();
        List<MsgObjectDTO> ret = null;
        return CompletableFuture.completedFuture(AsyncResult.success(ret));
    }

    @Async
    @Transactional(readOnly = true)
    public long deleteLetterList(long userId, List<TargetDTO> list){
        //TODO: Make Logic

        return 1;
    }

    @Async
    @Transactional(readOnly = true)
    public List<LetterTargetDTO> findUserListByUserNickname(String keyword) {
        //TODO: Make Logic
        List<LetterTargetDTO> ret = null;

        return ret;
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

    @Async
    @Transactional(readOnly = true)
    public List<MsgDTO> getSpecificLetterListByUserIdAndTargetId(long userId, SpecificMsgDTO target) {
        //TODO: Make Logic
        List<MsgDTO> ret = null;
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
