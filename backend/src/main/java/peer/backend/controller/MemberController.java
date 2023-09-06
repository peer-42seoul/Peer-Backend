package peer.backend.controller;

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
import peer.backend.service.EmailAuthService;
import peer.backend.service.MemberService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final EmailAuthService emailService;

    @GetMapping("/membership/email") // 메일을 전송하기 전, DB에서 메일이 있는지 확인
    public ResponseEntity<Object> sendEmail(@Valid @RequestBody EmailAddress address) {
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
        Message message = memberService.signUp(info);
        return new ResponseEntity<Object>(message.getDto(), message.getStatus());
    }
}
