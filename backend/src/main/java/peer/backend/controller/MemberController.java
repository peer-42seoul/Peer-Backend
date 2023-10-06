package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import peer.backend.dto.security.Message;
import peer.backend.dto.security.UserInfo;
import peer.backend.dto.security.request.EmailAddress;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.service.EmailAuthService;
import peer.backend.service.MemberService;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailAuthService emailService;

    @GetMapping("/membership/email") // 메일을 전송하기 전, DB에서 메일이 있는지 확인
    public ResponseEntity<Object> sendEmail(@Valid @RequestBody EmailAddress address) {
        String email = address.getAddress();

        if (this.memberService.emailDuplicationCheck(email)) {
            throw new ConflictException("이미 존재하는 이메일입니다!");
        }

        Message message = emailService.sendEmail(address.getAddress());
        return new ResponseEntity<Object>(message.getStatus());
    }

    @GetMapping("/membership/email/code")
    public ResponseEntity<Object> authenticate(@RequestParam(name = "code") String code) {
        Message message = emailService.authenticate(code);
        return new ResponseEntity<Object>(message.getStatus());
    }

    @PostMapping("/membership")
    public ResponseEntity<Object> signUp(@Valid @RequestBody UserInfo info) {
        // SQL 인젝션 체크
        User createdUser = memberService.signUp(info);
        return ResponseEntity.ok().build();
    }
}
