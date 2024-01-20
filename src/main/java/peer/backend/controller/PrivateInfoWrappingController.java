package peer.backend.controller;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.privateInfo.InitSecretDTO;
import peer.backend.dto.privateInfo.InitTokenDTO;
import peer.backend.dto.privateInfo.PrivateTokenDTO;
import peer.backend.service.PrivateInfoWrappingService;

@RestController
@RequiredArgsConstructor
@RequestMapping(PrivateInfoWrappingController.LETTER_URL)
public class PrivateInfoWrappingController {
    public static final String LETTER_URL = "api/v1/main";

    private final PrivateInfoWrappingService privateInfoWrappingService;

    @ApiOperation(value = "", notes = "민감한 정보를 전달 시작을 알리는 API. 최초 내용 전달을 위한 key와 code를 발급한다.")
    @PostMapping("/init")
    public ResponseEntity<?> initKeyForPrivacy() {
        InitSecretDTO result = this.privateInfoWrappingService.makeInitSecret();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "민감한 정보의 송신 용으로 사용되는 API 입니다. Seed와 Key를 제공합니다.")
    @PostMapping("/get")
    public ResponseEntity<?> getKeysForPrivacy(@RequestBody() InitTokenDTO data) {
        Claims resolved = this.privateInfoWrappingService.parseInitToken(data.getToken());
        return new ResponseEntity<>(resolved, HttpStatus.OK);
    }

    @ApiOperation(value = "", notes = "민감한 정보를 위한 수신합니다.")
    @PostMapping("/send")
    public ResponseEntity<?> sendTokenAndKey(@RequestBody() PrivateTokenDTO token) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
