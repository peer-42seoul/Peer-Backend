package peer.backend.controller;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.security.Message;
import peer.backend.dto.security.request.ToReissueTokens;
import peer.backend.dto.security.request.UserLoginRequest;
import peer.backend.dto.security.response.ErrorDto;
import peer.backend.dto.security.response.JwtDto;
import peer.backend.service.LoginService;

import javax.validation.Valid;
import java.util.*;

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

    @GetMapping("/accesstoken")
    public ResponseEntity<Object> reissueToken(@RequestBody ToReissueTokens tokens) {
        LinkedHashMap<String, Object> result;
        try {
            String token = tokens.getRefreshToken();
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String rowBody = token.split("\\.")[1];
            String body = new String(decoder.decode(rowBody));
            JSONParser parser = new JSONParser();
            JSONObject jsonBody = (JSONObject)parser.parse(body);
            Long userId = (Long)jsonBody.get("sub");
            Message message = loginService.reissue(userId, tokens.getRefreshToken());
            return new ResponseEntity<Object> (message.getDto(), message.getStatus());
        } catch (ParseException e) {
            ErrorDto errorDto = new ErrorDto("올바르지 않은 accessToken/refreshToekn입니다.", "/accesstoken");
            return new ResponseEntity<Object> (errorDto, HttpStatus.UNAUTHORIZED);
        }
    }
}
