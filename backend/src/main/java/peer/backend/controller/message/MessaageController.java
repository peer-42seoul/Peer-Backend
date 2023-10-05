package peer.backend.controller.message;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import peer.backend.service.message.MessageService;
import peer.backend.dto.message.MsgObjectDTO;
import peer.backend.dto.message.LetterTargetDTO;
import peer.backend.dto.message.MsgDTO;
import peer.backend.dto.message.targetDTO;
import peer.backend.dto.message.SpecificMsgDTO;
import peer.backend.dto.message.SpecificScrollMsgDTO;
import peer.backend.dto.message.MsgContentDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(MessaageController.LETTER_URL)
public class MessaageController {

    public static final String LETTER_URL = "api/v1/message";
    private final MessageService messageService;

    @ApiOperation(value="", notes="")
    @GetMapping("/list")
    public ResponseEntity<List<MsgObjectDTO>> getAllLetters(@RequestParam long userId){
        MsgObjectDTO one = new MsgObjectDTO();
        List<MsgObjectDTO> data = null;
        data.add(one);
        return new ResponseEntity<List<MsgObjectDTO>>(data, HttpStatus.OK);
    }

    @ApiOperation(value="", notes="")
    @DeleteMapping("/delete-message")
    public ResponseEntity<List<MsgObjectDTO>> deleteLetterList(@RequestBody List<targetDTO> body) {
        MsgObjectDTO one = new MsgObjectDTO();
        List<MsgObjectDTO> data = null;
        data.add(one);
        return new ResponseEntity<List<MsgObjectDTO>>(data, HttpStatus.OK);
    }

    @ApiOperation(value="", notes="")
    @GetMapping("/searching")
    public ResponseEntity<List<LetterTargetDTO>> searchNicknameInNewWindow(@RequestBody String keyword) {
        LetterTargetDTO one = new LetterTargetDTO();
        List<LetterTargetDTO> data = null;
        data.add(one);
        return new ResponseEntity<List<LetterTargetDTO>>(data, HttpStatus.OK);
    }

    @ApiOperation(value="", notes="")
    @PostMapping("/new-message")
    public ResponseEntity<List<MsgObjectDTO>> sendLetterInNewWindow() {
        MsgObjectDTO one = new MsgObjectDTO();
        List<MsgObjectDTO> data = null;
        data.add(one);
        return new ResponseEntity<List<MsgObjectDTO>>(data, HttpStatus.OK);
    }

    @ApiOperation(value="", notes="")
    @GetMapping("/conversation-list")
    public ResponseEntity<List<MsgDTO>> getSpecificLetters(@RequestParam Long target, @RequestBody SpecificMsgDTO body) {
        List<MsgDTO> data = null;
        return new ResponseEntity<List<MsgDTO>>(data, HttpStatus.OK);
    }

    @ApiOperation(value="", notes="")
    @GetMapping("/conversation-list/more")
    public ResponseEntity<List<MsgDTO>> getSpecificLettersInHistory(@RequestParam Long target, @RequestBody SpecificScrollMsgDTO body) {
        List<MsgDTO> data = null;
        return new ResponseEntity<List<MsgDTO>>(data, HttpStatus.OK);
    }

    @ApiOperation(value="", notes="")
    @PostMapping("/back-message")
    public ResponseEntity<Void> sendBackInSpecificLetter(@RequestParam Long target, @RequestBody MsgContentDTO body){
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

