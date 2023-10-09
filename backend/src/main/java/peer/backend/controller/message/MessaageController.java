package peer.backend.controller.message;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.asyncresult.AsyncResult;
import peer.backend.entity.message.MessageIndex;
import peer.backend.service.message.MessageMainService;
import peer.backend.dto.message.MsgObjectDTO;
import peer.backend.dto.message.LetterTargetDTO;
import peer.backend.dto.message.MsgDTO;
import peer.backend.dto.message.TargetDTO;
import peer.backend.dto.message.SpecificMsgDTO;
import peer.backend.dto.message.SpecificScrollMsgDTO;
import peer.backend.dto.message.MsgContentDTO;
import peer.backend.service.message.MessageSubService;

import java.sql.Wrapper;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping(MessaageController.LETTER_URL)
public class MessaageController {

    public static final String LETTER_URL = "api/v1/message";
    @Autowired
    private final MessageMainService messageMainService;
    private final MessageSubService messageSubService;

    @ApiOperation(value = "", notes = "유저의 쪽지 목록을 불러온다.")
    @GetMapping("/list")
    public ResponseEntity<List<MsgObjectDTO>> getAllLetters(@RequestParam long userId) {
        AsyncResult<List<MsgObjectDTO>> wrappedRet = null;
        List<MsgObjectDTO> ret = null;
        try {
            wrappedRet = this.messageMainService.getLetterListByUserId(userId).get();
        }
        catch (InterruptedException e) {
            // TODO: 스레드가 인터럽트 되었을 때 처리
        } catch (ExecutionException e) {
            // TODO: 비동기 작업 중 발생한 예외 처리
        }
        if (wrappedRet.getResult() != null)
            ret = wrappedRet.getResult();
        else if (wrappedRet.getException().getMessage().equals("User Not found"))
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<List<MsgObjectDTO>>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저의 쪽지 목록 중 일부를 삭제 한다.")
    @DeleteMapping("/delete-message")
    public ResponseEntity<List<MsgObjectDTO>> deleteLetterList(@RequestParam long userId, @RequestBody List<TargetDTO> body) {
        this.deleteLetterList(userId, body);

        AsyncResult<List<MsgObjectDTO>> wrappedRet = null;
        List<MsgObjectDTO> ret = null;
        try {
            wrappedRet = this.messageMainService.getLetterListByUserId(userId).get();
        }
        catch (InterruptedException e) {
            // TODO: 스레드가 인터럽트 되었을 때 처리
        } catch (ExecutionException e) {
            // TODO: 비동기 작업 중 발생한 예외 처리
        }
        if (wrappedRet.getResult() != null)
            ret = wrappedRet.getResult();
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<List<MsgObjectDTO>>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 넣은 키워드에 반응하여 해당하는 사용자를 호출합니다.")
    @GetMapping("/searching")
    public ResponseEntity<List<LetterTargetDTO>> searchNicknameInNewWindow(@RequestBody String keyword) {
        AsyncResult<List<LetterTargetDTO>> wrappedRet = null;
        List<LetterTargetDTO> ret = null;
        try {
            this.messageMainService.findUserListByUserNickname(keyword).get();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (wrappedRet.getResult() != null)
            ret = wrappedRet.getResult();
        else
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<List<LetterTargetDTO>>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 새로운 대상에게 메시지를 처음 보냅니다.")
    @PostMapping("/new-message")
    public ResponseEntity<List<MsgObjectDTO>> sendLetterInNewWindow(@RequestParam long userId, @RequestBody MsgContentDTO body) {
        // Message Index Create
        AsyncResult<MessageIndex> wrappedIndex = null;
        MessageIndex index = null;
        try {
            wrappedIndex = this.messageMainService.makeNewMessageIndex(userId, body).get();
        }
        catch (InterruptedException e) {
        // TODO: 스레드가 인터럽트 되었을 때 처리
        } catch (ExecutionException e) {
        // TODO: 비동기 작업 중 발생한 예외 처리
        }
        if (wrappedIndex.getResult() != null)
            index = wrappedIndex.getResult();
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        this.messageMainService.sendMessage(index, userId, body);

        // Get New Message List
        AsyncResult<List<MsgObjectDTO>> wrappedRet = null;
        List<MsgObjectDTO> ret = null;
        try {
            wrappedRet = this.messageMainService.getLetterListByUserId(userId).get();
        }
        catch (InterruptedException e) {
            // TODO: 스레드가 인터럽트 되었을 때 처리
        } catch (ExecutionException e) {
            // TODO: 비동기 작업 중 발생한 예외 처리
        }
        if (wrappedRet.getResult() != null)
            ret = wrappedRet.getResult();
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<List<MsgObjectDTO>>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 특정 대상과의 대화목록을 불러옵니다.")
    @GetMapping("/conversation-list")
    public ResponseEntity<List<MsgDTO>> getSpecificLetters(@RequestParam long userId, @RequestBody SpecificMsgDTO body) {
        List<MsgDTO> data = null;
        /**
         * getSpecificLetterListByUserIdAndTargetId
         */
        return new ResponseEntity<List<MsgDTO>>(data, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 특정 대상과의 대화목록의 과거 기록을 불러옵니다.")
    @GetMapping("/conversation-list/more")
    public ResponseEntity<List<MsgDTO>> getSpecificLettersInHistory(@RequestParam long userId, @RequestParam long page, @RequestBody SpecificScrollMsgDTO body) {
        List<MsgDTO> data = null;
        /**
         * getSpecificLetterUpByUserIdAndTargetId
         */
        return new ResponseEntity<List<MsgDTO>>(data, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "")
    @PostMapping("/back-message")
    public ResponseEntity<Void> sendBackInSpecificLetter(@RequestParam long uerId, @RequestBody MsgContentDTO body) {
        /**
         * sendMessage
         * getLetterListByUserId(?)
         */
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

