package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import peer.backend.dto.profile.request.PasswordRequest;
import peer.backend.entity.user.User;
import peer.backend.exception.ForbiddenException;
import peer.backend.service.MemberService;
import peer.backend.service.profile.PersonalInfoService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PersonalInfoController {

    private final PersonalInfoService personalInfoService;
    private final MemberService memberService;

    @ApiOperation(value = "C-MYPAGE-09", notes = "사용자 개인정보 조회하기")
    @GetMapping("/info")
    public ResponseEntity<Object> getPersonalInfo(Authentication auth) {
        return new ResponseEntity<>(personalInfoService.getPersonalInfo(auth), HttpStatus.OK);
    }

//    @ApiOperation(value = "C-MYPAGE-11", notes = "사용자 개인정보 비밀번호 변경하기")
//    @PutMapping("/info/password")
//    public ResponseEntity<Object> changePassword(Authentication auth,
//        @RequestBody @Valid PasswordRequest passwords) {
//        personalInfoService.changePassword(auth, passwords);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }

    @PostMapping("/info/check-password")
    public ResponseEntity<Void> checkPassword(@RequestBody @Valid PasswordRequest request,
        Authentication auth) {
        User user = User.authenticationToUser(auth);
        if (!this.memberService.verificationPassword(request.getPassword(), user.getPassword())) {
            throw new ForbiddenException("비밀번호가 일치하지 않습니다!");
        }
        return ResponseEntity.ok().build();
    }
}
