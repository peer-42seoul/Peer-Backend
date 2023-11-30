package peer.backend.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.entity.user.User;
import peer.backend.service.LoginService;

@RequiredArgsConstructor
@RestController
public class LogoutController {

    private final LoginService loginService;

    @ApiOperation(value = "C-SIGN-00", notes = "로그아웃.")
    @GetMapping("/api/v1/logout")
    public ResponseEntity<?> userLogout(Authentication authentication) {
        return loginService.logout(User.authenticationToUser(authentication));
    }
}
