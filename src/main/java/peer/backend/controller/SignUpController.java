package peer.backend.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import peer.backend.dto.PasswordDTO;
import peer.backend.dto.security.UserInfo;
import peer.backend.dto.security.request.EmailAddress;
import peer.backend.dto.security.request.EmailCode;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.ForbiddenException;
import peer.backend.service.EmailAuthService;
import peer.backend.service.MemberService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/signup")
@Slf4j
public class SignUpController {

    private final MemberService memberService;
    private final EmailAuthService emailService;

    @PostMapping("/email") // 메일을 전송하기 전, DB에서 메일이 있는지 확인
    public ResponseEntity<Object> sendEmail(@Valid @RequestBody EmailAddress address) {
        String email = address.getEmail();

        if (!this.memberService.emailDuplicationCheck(email)) {
            throw new ConflictException("이미 존재하는 이메일입니다!");
        }

//        Message message = emailService.sendEmail(address.getEmail(),
//            "회원가입을 위해 아래의 코드를 입력창에 입력해 주세요.\n\n%s\n");
        this.emailService.sendAuthCode(address.getEmail(),
            "회원가입을 위해 아래의 코드를 입력창에 입력해 주세요.\n\n%s\n");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/code")
    public ResponseEntity<Object> emailCodeVerification(@RequestBody EmailCode code) {
        this.emailService.emailCodeVerification(code.getEmail(), code.getCode());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/form")
    public ResponseEntity<Object> signUp(@Valid @RequestBody UserInfo info) {
        // SQL 인젝션 체크
        User createdUser = memberService.signUp(info);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<Object> withdrawal(@RequestBody PasswordDTO password,
        Authentication authentication) {
        User user = User.authenticationToUser(authentication);
        if (!this.memberService.verificationPassword(password.getPassword(), user.getPassword())) {
            throw new ForbiddenException("비밀번호가 잘못되었습니다!");
        }
        this.memberService.deleteUser(user);
        return ResponseEntity.ok().build();
    }
}
