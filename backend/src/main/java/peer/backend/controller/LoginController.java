package peer.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.security.request.UserLoginRequest;
import peer.backend.dto.security.response.JwtDto;
import peer.backend.service.LoginService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class  LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody UserLoginRequest userLoginRequestDto) {

        try {
            JwtDto jwtDto = loginService.login(userLoginRequestDto.getUserEmail(), userLoginRequestDto.getPassword());
            return ResponseEntity.ok(jwtDto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("errorMsg", e.getMessage());
            return new ResponseEntity<Object>(error, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/userInfo")
    public ResponseEntity<String> userInfo(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new ResponseEntity<String>(userDetails.getUsername(), HttpStatus.OK);
    }
}
