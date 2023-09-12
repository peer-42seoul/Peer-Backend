package peer.backend.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.security.request.LogoutRequest;
import peer.backend.dto.security.request.ToReissueToken;
import peer.backend.dto.security.request.UserLoginRequest;
import peer.backend.dto.security.response.JwtDto;
import peer.backend.exception.UnauthorizedException;
import peer.backend.service.LoginService;

import javax.validation.Valid;
import java.util.*;

@RequiredArgsConstructor
@RestController
public class SignController {
    private final LoginService loginService;

    @ApiOperation(value = "C-SIGN-01", notes = "로그인.")
    @GetMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        JwtDto jwtDto = loginService.login(userLoginRequest.getUserEmail(), userLoginRequest.getPassword());
        maps.put("isLogin", "true");
        maps.put("userId", jwtDto.getUserId());
        maps.put("accessToken", jwtDto.getAccessToken());
        maps.put("refreshToken", jwtDto.getRefreshToken());
        return ResponseEntity.ok(maps);
    }

    @ApiOperation(value = "C-SIGN-00", notes = "로그아웃.")
    @GetMapping("/user-logout")
    public ResponseEntity<?> userLogout(Authentication authentication, @Valid @RequestBody LogoutRequest logoutRequest) {
        return loginService.logout(logoutRequest, authentication);
    }

    @ApiOperation(value = "C-SIGN-09", notes = "accessToken 만료시에 다시 accessToken을 발급받습니다.")
    @GetMapping("/access-token")
    public ResponseEntity<?> reissueToken(@RequestBody ToReissueToken refreshToken) {
        try {
            String token = refreshToken.getRefreshToken();
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String rowBody = token.split("\\.")[1];
            String body = new String(decoder.decode(rowBody));
            JSONParser parser = new JSONParser();
            JSONObject jsonBody = (JSONObject)parser.parse(body);
            Long userId = (Long)jsonBody.get("sub");
            String accessToken = loginService.reissue(userId, refreshToken.getRefreshToken());
            HashMap<String, String> maps = new HashMap<>();
            maps.put("accessToken", accessToken);
            return new ResponseEntity<Object> (maps, HttpStatus.OK);
        } catch (ParseException e) {
            throw new UnauthorizedException("토큰이 유효하지 않습니다.");
        }
    }
}
