package peer.backend.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectDeletedException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import peer.backend.comparator.MessagePieceComparator;
import peer.backend.dto.asyncresult.AsyncResult;
import peer.backend.dto.message.*;
import peer.backend.dto.security.Message;
import peer.backend.entity.message.MessageIndex;
import peer.backend.entity.message.MessagePiece;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.message.MessageIndexRepository;
import peer.backend.repository.message.MessagePieceRepository;
import peer.backend.repository.user.UserRepository;

import javax.swing.text.html.Option;
import java.net.SocketOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@EnableWebMvc
@Slf4j
@Component
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
     * @param msgOwner : 구체적으로 Letter 목록을 요청하는 대상
     * @return
     */
    @Async
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<AsyncResult<List<MsgObjectDTO>>> getLetterListByUserId(User msgOwner) {

        List<MessageIndex> msgList = null;
        try {
            // msgIndex Get
            msgList = this.subService.getMessageIndexList(msgOwner.getId());
        } catch (NoSuchElementException e) {
            return CompletableFuture.completedFuture((AsyncResult.failure(e)));
        }

        List<MsgObjectDTO> retList = new ArrayList<>();
        User target = null;
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Index 기준으로 반복문으로 MsgObject 작성 시작
        for (MessageIndex msg : msgList) {
            // 대화
              Page<MessagePiece> data = this.pieceRepository.findTopByTargetConversationIdOrderByCreatedAtDesc(msg.getConversationId(), pageable);
              MessagePiece conversation = data.getContent().get(0);
            // 상대방 확인
            if (msg.getUserIdx1().equals(msgOwner.getId())) {
                if (msg.isUser1delete())
                    continue;
                else
                    target = this.userRepository.findById(msg.getUserIdx2()).get();
            } else if (msg.getUserIdx2().equals(msgOwner.getId())) {
                if (msg.isUser2delete())
                    continue;
                else
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
     * @param userId : 삭제하려는 당사자
     * @param list : 삭제할 대화 목록
     * @return
     */
    @Async
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public CompletableFuture<AsyncResult<Long>> deleteLetterList(long userId, List<TargetDTO> list){
        Long ret;
        ret = 0L;

        List<MessageIndex> targetsData = this.indexRepository.findByUserId(userId).orElseGet(() -> null);
        if (targetsData == null)
            return CompletableFuture.completedFuture(AsyncResult.success(0L));
        boolean check = false;
        for (TargetDTO target : list) {
            for (MessageIndex data : targetsData) {
                if (data.getUserIdx1().equals(target.getTargetId())) {
                    data.setUser2delete(true);
                    check = true;
                }
                if (data.getUserIdx2().equals(target.getTargetId())) {
                    data.setUser1delete(true);
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
     * @param keyword : 검색할 키워드
     * @return
     */
    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<AsyncResult<List<LetterTargetDTO>>> findUserListByUserNickname(KeywordDTO keyword, User userData) {
        List<User> raw = this.userRepository.findByKeyWord(keyword.getKeyword()).orElseGet(() -> null);
        if (raw == null)
            return CompletableFuture.completedFuture(AsyncResult.success(null));
        List<LetterTargetDTO> ret = new ArrayList<>();
        for (User candidate: raw) {
            if (candidate.getId().equals(userData.getId()))
                continue ;
            LetterTargetDTO data = new LetterTargetDTO();
            try {
                data = LetterTargetDTO.builder().
                        targetId(candidate.getId()).
                        targetEmail(candidate.getEmail()).
                        targetNickname(candidate.getNickname()).
                        targetProfile(candidate.getImageUrl()).build();
                ret.add(data);
            } catch (Exception e) {
                return CompletableFuture.completedFuture(AsyncResult.failure(e));
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
     * @param auth : 사용자의 userID
     * @param message : 대상이 되는 대상에게 메시지를 보내는 경우
     * @return : 성공 여부에 따라 messageIndex 객체를 반환 받는다.
     */
    @Async
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<AsyncResult<MessageIndex>> makeNewMessageIndex(Authentication auth, MsgContentDTO message) {
        User owner = User.authenticationToUser(auth);
        try {
            this.subService.checkMessageIndexExistOrNot(owner.getId(), message.getTargetId());
        } catch (Exception e){
            return CompletableFuture.completedFuture(AsyncResult.failure(e));
        }
        User target;
        Optional<User> data = this.userRepository.findById(message.getTargetId());
        try {
            target = data.orElseThrow(() -> new Exception("User not found"));
        } catch (Exception e) {
//            System.out.println("Check to here2" + e.getMessage());
            return CompletableFuture.completedFuture((AsyncResult.failure(e)));
        }
        MessageIndex saved = this.subService.saveNewData(owner, target);
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
     * @param auth : 당사자
     * @param message : 보낼 메시지를 담고 있다.
     * @return true 면 정상 저장. 만약 실패하면 false 를 반환한다.
     */

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Msg sendMessage(MessageIndex index, Authentication auth, MsgContentDTO message) {
        User msgOwner = User.authenticationToUser(auth);

        MessagePiece letter = MessagePiece.builder().
                targetConversationId(index.getConversationId()).
                senderNickname(msgOwner.getNickname()).
                senderId(msgOwner.getId()).
                text(message.getContent()).
                index(index).
                build();

        MessagePiece rawRet = null;
        try {
            rawRet = this.pieceRepository.save(letter);
        } catch (OptimisticLockingFailureException e) {
            return null;
        }

        try {
           if (index.getUserIdx1().equals(msgOwner.getId()))
            {
                long unread = index.getUnreadMessageNumber2();
                unread += 1;
                index.setUnreadMessageNumber2(unread);
            }
            else if (index.getUserIdx2().equals(msgOwner.getId()))
            {
                long unread = index.getUnreadMessageNumber1();
                unread += 1;
                index.setUnreadMessageNumber1(unread);
            }
            this.indexRepository.save(index);
        } catch (OptimisticLockingFailureException e) {
            return null;
        }

//        String sql = "SELECT * FROM message_piece WHERE target_conversation_id = :conversationId AND msg_id < :msgId ORDER BY created_at DESC LIMIT 2";
//        List<MessagePiece> talks = this.subService.executeNativeSQLQueryForMessagePiece(sql, Map.of("conversationId", index.getConversationId(), "msgId", rawRet.getMsgId()));
//        boolean isEnd;
//        if (talks.size() == 0) {
//            isEnd = true;
//        }
//        else {
//            isEnd = false;
//        }
        Msg ret = Msg.builder().msgId(rawRet.getMsgId()).
                end(false).
                content(rawRet.getText()).
                date(this.subService.makeFormattedDate(rawRet.getCreatedAt())).
                userId(rawRet.getSenderId()).
                build();

        return ret;
    }

    /**
     * 메시지가 존재하며, 구체적인 대화 목록에서 대화를 진행하면 사용하는 메소드
     * @param auth : 메시지를 보낸 당사자
     * @param message : 메시지 내용
     * @return
     */
    @Transactional(readOnly = false)
    public Msg sendMessage(Authentication auth, MsgContentDTO message) {
        long targetId = message.getTargetId();
        MessageIndex index;
        try {
            index = this.indexRepository.findByUserIdx(User.authenticationToUser(auth).getId(), targetId).orElseThrow(() ->new NoSuchElementException("There is no talks"));
        } catch (NoSuchElementException e)
        {
            return null;
        }
        return this.sendMessage(index, auth, message);
    }

    /**
     * OutLine : userId 와 구체적인 메시지 DTO를 통해 쪽지들을 불러온다.
     * Logic :
     * 0. msgIndex에서 해당 유저가 이미 삭제 처리를 한 상태인지 체크한다(했으면 반환하지 않는다)
     * 1. targetId, Conversation Id를 통해 쪽지 데이터를 전체 들고옴.
     * 2. 데이터를 MsgDTO 에 맞춰 가공 처리한다.
     * @param auth : 당사자
     * @param target : 구체적인 대화 목록을 불러 온다.
     * @return
     */
    @Async
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<AsyncResult<MsgListDTO>> getSpecificLetterListByUserIdAndTargetId(Authentication auth, SpecificMsgDTO target) {
        // MessageIndex 찾기
        MessageIndex targetIndex = this.indexRepository.findTopByConversationId(target.getConversationalId()).orElseGet(() -> null);
        User requestingUser = User.authenticationToUser(auth);
        long userId = requestingUser.getId();
        try {
            if (targetIndex == null)
                throw new NoSuchElementException("There is no talks");
            if (targetIndex.getUserIdx1().equals(userId)) {
                if (targetIndex.isUser1delete())
                    throw new ObjectDeletedException("Messages are deleted", MessageIndex.class, "MessageIndex");
            } else if (targetIndex.getUserIdx2().equals(userId)) {
                if (targetIndex.isUser2delete())
                    throw new ObjectDeletedException("Messages are deleted", MessageIndex.class, "MessageIndex");
            }
        } catch (Exception e) {
            return CompletableFuture.completedFuture(AsyncResult.failure(e));
        }

        // index read 수정
        if (targetIndex.getUserIdx1().equals(userId)){
            if(targetIndex.getUnreadMessageNumber1() != 0)
            {
                targetIndex.setUnreadMessageNumber1(0L);
                this.indexRepository.save(targetIndex);
            }
        } else {
            if(targetIndex.getUnreadMessageNumber2() != 0)
            {
                targetIndex.setUnreadMessageNumber2(0L);
                this.indexRepository.save(targetIndex);
            }
        }

        // MessagePiece의 List 찾기
        String sql = "SELECT * FROM message_piece WHERE target_conversation_id = :conversationId ORDER BY created_at DESC LIMIT 21";
        List<MessagePiece> talks = this.subService.executeNativeSQLQueryForMessagePiece(sql, Map.of("conversationId", target.getConversationalId()));

        // Msg 객체 덩어리로 만들기
        MsgListDTO ret = new MsgListDTO();
        List<Msg> innerData = new ArrayList<>();
        innerData = this.subService.makeMsgDataWithMessagePiece(talks);

        // User 객체들 찾기
        User targetUser = null;
        Optional<User> rawTarget;
        try {
            rawTarget = this.userRepository.findById(target.getTargetId());
            if (rawTarget.isEmpty())
                throw new NotFoundException("There is no a specific user");
        } catch (Exception e) {
            return CompletableFuture.completedFuture(AsyncResult.failure(e));
        }
        targetUser = rawTarget.get();

        // User 객체, List<Msg> 객체로 MsgListDTO 만들기
        ret = this.subService.makeMsgDTO(requestingUser, targetUser, innerData);

        return CompletableFuture.completedFuture(AsyncResult.success(ret));
    }

    /**
     * OutLine :무한스크롤로 과거의 메시지를 기준 단위로 받아온다.
     * Logic :
     * 0. msgIndex에서 해당 유저가 이미 삭제 처리를 한 상태인지 체크한다(했으면 반환하지 않는다)
     * 1. ConversationId, earlyMsgId 를 통해 쪽지 데이터를 전체 들고옴.
     * 2. 데이터를 MsgDTO 에 맞춰 가공 처리한다.
     * @param auth : 당사자
     * @param target :
     * @return
     */
    @Async
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<AsyncResult<MsgListDTO>> getSpecificLetterUpByUserIdAndTargetId(Authentication auth, SpecificScrollMsgDTO target) {
        // MessageIndex 찾기
        MessageIndex targetIndex = this.indexRepository.findTopByConversationId(target.getConversationId()).orElseGet(() -> null);
        User requestingUser = User.authenticationToUser(auth);
        long userId = requestingUser.getId();
        try {
            if (targetIndex == null)
                throw new NoSuchElementException("There is no Talks");
            if (targetIndex.getUserIdx1().equals(userId)) {
                if (targetIndex.isUser1delete())
                    throw new ObjectDeletedException("Messages are deleted", MessageIndex.class, "MessageIndex");
            } else if (targetIndex.getUserIdx2().equals(userId)) {
                if (targetIndex.isUser2delete())
                    throw new ObjectDeletedException("Messages are deleted", MessageIndex.class, "MessageIndex");
            } } catch (Exception e) {
            return CompletableFuture.completedFuture(AsyncResult.failure(e));
        }

        // MessagePiece의 List 찾기
        String sql = "SELECT * FROM message_piece WHERE target_conversation_id = :conversationId AND msg_id < :earlyMsgId ORDER BY created_at DESC LIMIT 21";
        List<MessagePiece> talks = this.subService.executeNativeSQLQueryForMessagePiece(sql, Map.of("conversationId", target.getConversationId(), "earlyMsgId", target.getEarlyMsgId()));
//        talks.sort(new MessagePieceComparator());

        // Msg 객체 덩어리로 만들기
        MsgListDTO ret = new MsgListDTO();
        List<Msg> innerData = new ArrayList<>();
        innerData = this.subService.makeMsgDataWithMessagePiece(talks);

        // User 객체들 찾기
        User targetUser = null;
        Optional<User> rawTarget;
        try {
            rawTarget = this.userRepository.findById(target.getTargetId());
            if (rawTarget.isEmpty())
                throw new NotFoundException("There is no a specific user");
        } catch (Exception e) {
            return CompletableFuture.completedFuture(AsyncResult.failure(e));
        }
        targetUser = rawTarget.get();

        // User 객체, List<Msg> 객체로 MsgListDTO 만들기
        ret = this.subService.makeMsgDTO(requestingUser, targetUser, innerData);

        return CompletableFuture.completedFuture(AsyncResult.success(ret));
    }
}
