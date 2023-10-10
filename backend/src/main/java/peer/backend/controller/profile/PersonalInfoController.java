package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import peer.backend.dto.profile.PasswordRequest;
import peer.backend.service.profile.PersonalInfoService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PersonalInfoController {
    private final PersonalInfoService personalInfoService;
    @ApiOperation(value = "C-MYPAGE-09", notes = "사용자 개인정보 조회하기")
    @GetMapping("/info")
    public ResponseEntity<Object> getPersonalInfo(Authentication auth) {
        return new ResponseEntity<> (personalInfoService.getPersonalInfo(auth.getName()), HttpStatus.OK);
    }

    @ApiOperation(value = "C-MYPAGE-11", notes = "사용자 개인정보 비밀번호 변경하기")
    @PutMapping("/info/password")
    public ResponseEntity<Object> changePassword(Authentication auth, @RequestBody PasswordRequest passwords) {
        personalInfoService.changePassword(auth.getName(), passwords);
        return new ResponseEntity<> (HttpStatus.CREATED);
    }
}
