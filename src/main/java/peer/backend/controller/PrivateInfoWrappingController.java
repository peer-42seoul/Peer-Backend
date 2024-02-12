package peer.backend.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.privateinfo.InitSecretDTO;
import peer.backend.dto.privateinfo.InitTokenDTO;
import peer.backend.dto.privateinfo.MainSeedDTO;
import peer.backend.dto.privateinfo.PrivateDataDTO;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.service.PrivateInfoWrappingService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(PrivateInfoWrappingController.LETTER_URL)
public class PrivateInfoWrappingController {
    public static final String LETTER_URL = "api/v1/main";

    private final PrivateInfoWrappingService privateInfoWrappingService;

    @ApiOperation(value = "", notes = "민감한 정보를 전달 시작을 알리는 API. 최초 내용 전달을 위한 key와 code를 발급한다.")
    @GetMapping("/init")
    public ResponseEntity<?> initKeyForPrivacy() {
        InitSecretDTO result = this.privateInfoWrappingService
                .makeInitSecret();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "민감한 정보의 송신 용으로 사용되는 API 입니다. Seed와 Key를 제공합니다.")
    @PostMapping("/get")
    public ResponseEntity<?> getKeysForPrivacy
            (@RequestBody() InitTokenDTO data) {
        MainSeedDTO resolved;
        try {
            resolved = this.privateInfoWrappingService
                    .parseInitToken(data);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(resolved, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "민감한 정보를 위한 수신합니다.")
    @PostMapping("/receive")
    public ResponseEntity<?> sendTokenAndKey(Authentication auth, @RequestBody() PrivateDataDTO token, HttpServletResponse response) {
        User user = null;
        if (auth != null)
            user = User.authenticationToUser(auth);
        return this.privateInfoWrappingService.processDataFromToken(user, token, response);
    }
}
