package peer.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.security.request.UserLoginRequest;
import peer.backend.service.LoginService;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class  LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginRequest userLoginRequestDto) {

        String token = loginService.login(userLoginRequestDto.getUserEmail(), userLoginRequestDto.getPassword());
        return ResponseEntity.ok(token);
    }

//    @GetMapping("/logout")
//    public ResponseEntity<String> logout() {
//
//    }
//    @GetMapping("/login/success")
//    public ResponseEntity<String> loginSuccess() {
//        return ResponseEntity.ok("login good!!");
//    }

}
