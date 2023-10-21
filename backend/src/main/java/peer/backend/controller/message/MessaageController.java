package peer.backend.controller.message;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.IBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.asyncresult.AsyncResult;
import peer.backend.dto.message.*;
import peer.backend.entity.message.MessageIndex;
import peer.backend.service.message.MessageMainService;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping(MessaageController.LETTER_URL)
public class MessaageController {

    public static final String LETTER_URL = "api/v1/message";

    private final MessageMainService messageMainService;

    @ApiOperation(value = "", notes = "유저의 쪽지 목록을 불러온다.")
    @GetMapping("/list")
    public ResponseEntity<List<MsgObjectDTO>> getAllLetters(Principal data, @RequestParam long userId) {
        AsyncResult<List<MsgObjectDTO>> wrappedRet;
        List<MsgObjectDTO> ret;
        try {
            wrappedRet = this.messageMainService.getLetterListByUserId(userId).get();
        }
        catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
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
    public ResponseEntity<List<MsgObjectDTO>> deleteLetterList(Principal data, @RequestParam long userId, @RequestBody List<TargetDTO> body) {
        this.messageMainService.deleteLetterList(userId, body);

        AsyncResult<List<MsgObjectDTO>> wrappedRet;
        List<MsgObjectDTO> ret;
        try {
            wrappedRet = this.messageMainService.getLetterListByUserId(userId).get();
        }
        catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
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
    public ResponseEntity<List<LetterTargetDTO>> searchNicknameInNewWindow(Principal data, @RequestBody KeywordDTO keyword) {
        System.out.println(keyword.getKeyword());
        AsyncResult<List<LetterTargetDTO>> wrappedRet= new AsyncResult<>();
        List<LetterTargetDTO> ret;
        try {
            wrappedRet = this.messageMainService.findUserListByUserNickname(keyword).get();
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            ret = wrappedRet.getResult();
        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
        System.out.println(ret);
        return new ResponseEntity<List<LetterTargetDTO>>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 새로운 대상에게 메시지를 처음 보냅니다.")
    @PostMapping("/new-message")
    public ResponseEntity<List<MsgObjectDTO>> sendLetterInNewWindow(Principal data, @RequestParam long userId, @RequestBody MsgContentDTO body) {
        // Message Index Create
        AsyncResult<MessageIndex> wrappedIndex;
        MessageIndex index;
        try {
            wrappedIndex = this.messageMainService.makeNewMessageIndex(userId, body).get();
        }
        catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            System.out.println("여기 어떰?!" + e);
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!wrappedIndex.isSuccess())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        else
            index = wrappedIndex.getResult();
        this.messageMainService.sendMessage(index, userId, body);

        // Get New Message List
        AsyncResult<List<MsgObjectDTO>> wrappedRet;
        List<MsgObjectDTO> ret;
        try {
            wrappedRet = this.messageMainService.getLetterListByUserId(userId).get();
        }
        catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            System.out.println("여기 어떰?!");
            e.printStackTrace();
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
    public ResponseEntity<MsgListDTO> getSpecificLetters(Principal data, @RequestParam long userId, @RequestBody SpecificMsgDTO body) {
        AsyncResult<MsgListDTO> wrappedData;
        try {
            wrappedData = this.messageMainService.getSpecificLetterListByUserIdAndTargetId(userId, body).get();
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        MsgListDTO ret = wrappedData.getResult();
        if (ret == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 특정 대상과의 대화목록의 과거 기록을 불러옵니다.")
    @PostMapping("/conversation-list/more")
    public ResponseEntity<MsgListDTO> getSpecificLettersInHistory(Principal data, @RequestParam long userId, @RequestBody SpecificScrollMsgDTO body) {
        AsyncResult<MsgListDTO> wrappedData;
        try {
            wrappedData =this.messageMainService.getSpecificLetterUpByUserIdAndTargetId(userId, body).get();
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        MsgListDTO ret = wrappedData.getResult();
        if (ret == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "유저가 특정 대상과의 대화목록에서 메시지를 전달합니다. ")
    @PostMapping("/back-message")
    public ResponseEntity<Void> sendBackInSpecificLetter(Principal data, @RequestParam long userId, @RequestBody MsgContentDTO body) {

        if (!this.messageMainService.sendMessage(userId, body))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

