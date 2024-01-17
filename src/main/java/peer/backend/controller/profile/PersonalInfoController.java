package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import peer.backend.dto.profile.request.ChangePasswordRequest;
import peer.backend.dto.profile.request.PasswordRequest;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.ForbiddenException;
import peer.backend.service.MemberService;
import peer.backend.service.profile.PersonalInfoService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/info")
public class PersonalInfoController {

    private final PersonalInfoService personalInfoService;
    private final MemberService memberService;

    @ApiOperation(value = "C-MYPAGE-09", notes = "사용자 개인정보 조회하기")
    @GetMapping()
    public ResponseEntity<Object> getPersonalInfo(Authentication auth) {
        return new ResponseEntity<>(personalInfoService.getPersonalInfo(auth), HttpStatus.OK);
    }

    @PostMapping("/check-password")
    public ResponseEntity<Map<String, String>> checkPassword(
        @RequestBody @Valid PasswordRequest request,
        Authentication auth) {
        final int PASSWORD_CHANGE_CODE_EXPIRATION_MINUTE = 5;
        User user = User.authenticationToUser(auth);

        if (!this.memberService.verificationPassword(request.getPassword(), user.getPassword())) {
            throw new ForbiddenException("비밀번호가 일치하지 않습니다!");
        }

        String uuid = this.personalInfoService.getChangePasswordCode(user.getId());

        HashMap<String, String> body = new HashMap<>();
        body.put("code", uuid);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request,
        Authentication auth) {
        User user = User.authenticationToUser(auth);
        if (!this.personalInfoService.checkChangePasswordCode(user.getId(), request.getCode())) {
            throw new ForbiddenException("유효하지 않은 코드입니다!");
        }
        if (this.memberService.verificationPassword(request.getPassword(), user.getPassword())) {
            throw new ConflictException("현재 비밀번호와 일치합니다!");
        }
        this.personalInfoService.changePassword(user, request.getPassword());
        return ResponseEntity.ok().build();
    }
}
