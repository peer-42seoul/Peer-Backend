package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import peer.backend.dto.security.Message;
import peer.backend.dto.security.UserInfo;
import peer.backend.dto.security.request.EmailAddress;
import peer.backend.dto.security.request.EmailCode;
import peer.backend.entity.user.User;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.exception.ConflictException;
import peer.backend.service.EmailAuthService;
import peer.backend.service.MemberService;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailAuthService emailService;

    @PostMapping("/membership/email") // 메일을 전송하기 전, DB에서 메일이 있는지 확인
    public ResponseEntity<Object> sendEmail(@Valid @RequestBody EmailAddress address) {
        String email = address.getEmail();

        if (this.memberService.emailDuplicationCheck(email)) {
            throw new ConflictException("이미 존재하는 이메일입니다!");
        }

        Message message = emailService.sendEmail(address.getEmail());
        return new ResponseEntity<Object>(message.getStatus());
    }

    @PostMapping("/membership/email/code")
    public ResponseEntity<Object> emailCodeVerification(@RequestBody EmailCode code) {
        this.emailService.emailCodeVerification(code.getEmail(), code.getCode());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/membership")
    public ResponseEntity<Object> signUp(@Valid @RequestBody UserInfo info) {
        // SQL 인젝션 체크
        User createdUser = memberService.signUp(info);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/membership/withdrawal")
    public ResponseEntity<Object> withdrawal(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        this.memberService.deleteUser(user);
        return ResponseEntity.ok().build();
    }
}
