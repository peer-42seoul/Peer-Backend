package peer.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import peer.backend.dto.Message;
import peer.backend.dto.UserInfo;
import peer.backend.service.MemberService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService service;

    @PostMapping("/membership")
    public ResponseEntity<Object> signUp(@Valid @RequestBody UserInfo login) {
        // SQL 인젝션 체크
        Message message = service.signUp(login);
        return new ResponseEntity<Object>(message.getMessage(), message.getStatus());
    }
}
