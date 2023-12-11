package peer.backend.controller.message;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.asyncresult.AsyncResult;
import peer.backend.dto.message.*;
import peer.backend.entity.message.MessageIndex;
import peer.backend.entity.user.User;
import peer.backend.exception.AlreadyDeletedException;
import peer.backend.service.message.MessageMainService;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MessaageController.LETTER_URL)
public class MessaageController {

    public static final String LETTER_URL = "api/v1/message";

    private final MessageMainService messageMainService;

    @ApiOperation(value = "", notes = "유저의 쪽지 목록을 불러온다.")
//    @ApiImplicitParam(name = "id", value = "사용자 아이디", required = true, dataType = "number", paramType = "Param", defaultValue = "None")
    @GetMapping("/list")
    public ResponseEntity<List<MsgObjectDTO>> getAllLetters(Authentication auth) {
        AsyncResult<List<MsgObjectDTO>> wrappedRet;
        List<MsgObjectDTO> ret;
        try {
            wrappedRet = this.messageMainService.getLetterListByUserId(User.authenticationToUser(auth)).get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interruption happens in getAllLetters");
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            log.error("Problem is happened in getAllLetters");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (wrappedRet.isSuccess() && wrappedRet.getException() == null)
            ret = wrappedRet.getResult();
        else if (wrappedRet.getException() == null)
            ret = null;
        else if (wrappedRet.getException().getMessage().equals("User Not found"))
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저의 쪽지 목록 중 일부를 삭제 한다.")
    @DeleteMapping("/delete-message")
    public ResponseEntity<List<MsgObjectDTO>> deleteLetterList(Authentication auth, @RequestBody TargetDTO body) {
        CompletableFuture<AsyncResult<Long>> deleted;
        deleted = this.messageMainService.deleteLetterList(User.authenticationToUser(auth).getId(), body);
        try {
            Long finished = deleted.get().getResult();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        AsyncResult<List<MsgObjectDTO>> wrappedRet;
        List<MsgObjectDTO> ret;
        try {
            wrappedRet = this.messageMainService.getLetterListByUserId(User.authenticationToUser(auth)).get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interruption happens in getLetterListByUserId");
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            log.error("Problem is happened in getLetterListByUserId");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (wrappedRet.getResult() != null)
            ret = wrappedRet.getResult();
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 넣은 키워드에 반응하여 해당하는 사용자를 호출합니다.")
    @PostMapping("/searching")
    public ResponseEntity<List<LetterTargetDTO>> searchNicknameInNewWindow(Authentication data, @RequestBody @Valid KeywordDTO keyword) {
        AsyncResult<List<LetterTargetDTO>> wrappedRet= new AsyncResult<>();
        List<LetterTargetDTO> ret;
        User user = User.authenticationToUser(data);
        try {
            wrappedRet = this.messageMainService.findUserListByUserNickname(keyword, user).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interruption happens in findUserListByUserNickname");
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            log.error("Problem is happened in findUserListByUserNickname");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            ret = wrappedRet.getResult();
        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<List<LetterTargetDTO>>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 새로운 대상에게 메시지를 처음 보냅니다.")
    @PostMapping("/new-message")
    public ResponseEntity<?> sendLetterInNewWindow(Authentication auth, @RequestBody MsgContentDTO body) {
        // Message Index Create
        AsyncResult<MessageIndex> wrappedIndex = new AsyncResult<>();
        MessageIndex index;
        try {
            wrappedIndex = this.messageMainService.makeNewMessageIndex(auth, body).get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interruption happens in makeNewMessageIndex");
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            log.error("Problem is happened in makeNewMessageIndex");
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if (!wrappedIndex.isSuccess())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        else
            index = wrappedIndex.getResult();
        this.messageMainService.sendMessage(index, auth, body);

        // Get New Message List
        AsyncResult<List<MsgObjectDTO>> wrappedRet;
        List<MsgObjectDTO> ret;
        try {
            wrappedRet = this.messageMainService.getLetterListByUserId(User.authenticationToUser(auth)).get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interruption happens in getLetterListByUserId");
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            log.error("Problem is happened in getLetterListByUserId");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (wrappedRet.getResult() != null)
            ret = wrappedRet.getResult();
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 특정 대상과의 대화목록을 불러옵니다.")
    @PostMapping("/conversation-list")
    public ResponseEntity<MsgListDTO> getSpecificLetters(Authentication auth, @RequestBody SpecificMsgDTO body) {
        AsyncResult<MsgListDTO> wrappedData;
        try {
            wrappedData = this.messageMainService.getSpecificLetterListByUserIdAndTargetId(auth, body).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interruption happens in getSpecificLetterListByUserIdAndTargetId");
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            log.error("Problem is happened in getSpecificLetterListByUserIdAndTargetId");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!wrappedData.isSuccess())
        {
            if (wrappedData.getException().getMessage().equals("target user deleted message")) {
                return new ResponseEntity<>(HttpStatus.GONE);
            }
            else if (wrappedData.getException().getMessage().equals("Messages are deleted")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        MsgListDTO ret = wrappedData.getResult();
        if (ret == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 특정 대상과의 대화목록의 과거 기록을 불러옵니다.")
    @PostMapping("/conversation-list/more")
    public ResponseEntity<MsgListDTO> getSpecificLettersInHistory(Authentication auth, @RequestBody SpecificScrollMsgDTO body) {
        AsyncResult<MsgListDTO> wrappedData;
        try {
            wrappedData =this.messageMainService.getSpecificLetterUpByUserIdAndTargetId(auth, body).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interruption happens in getSpecificLetterUpByUserIdAndTargetId");
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            log.error("Problem is happened in getSpecificLetterUpByUserIdAndTargetId");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        MsgListDTO ret = wrappedData.getResult();
        if (ret == null) {
            log.error("Problem is happened in getSpecificLetterUpByUserIdAndTargetId");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 특정 대상과의 대화목록에서 메시지를 전달합니다. ")
    @PostMapping("/back-message")
    public ResponseEntity<Msg> sendBackInSpecificLetter(Authentication auth, @RequestBody @Valid MsgContentDTO body) {
        Msg ret;
        try {
            ret = this.messageMainService.sendMessage(auth, body);
        } catch (AlreadyDeletedException e) {
            return new ResponseEntity<>(HttpStatus.GONE);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ret, HttpStatus.CREATED);
    }
}

