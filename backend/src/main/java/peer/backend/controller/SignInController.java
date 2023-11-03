package peer.backend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.security.Message;
import peer.backend.dto.security.request.EmailAddress;
import peer.backend.dto.security.request.EmailCode;
import peer.backend.dto.security.request.LogoutRequest;
import peer.backend.dto.security.request.ToReissueToken;
import peer.backend.dto.security.request.UserLoginRequest;
import peer.backend.dto.security.response.JwtDto;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.NotFoundException;
import peer.backend.exception.UnauthorizedException;
import peer.backend.service.EmailAuthService;
import peer.backend.service.LoginService;

import javax.validation.Valid;
import java.util.*;
import peer.backend.service.MemberService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/signin")
public class SignInController {

    private final LoginService loginService;
    private final MemberService memberService;
    private final EmailAuthService emailService;

    @ApiOperation(value = "C-SIGN-01", notes = "로그인.")
    @PostMapping()
    public ResponseEntity<Object> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        JwtDto jwtDto = loginService.login(userLoginRequest.getUserEmail(),
            userLoginRequest.getPassword());
        maps.put("isLogin", "true");
        maps.put("userId", jwtDto.getUserId());
        maps.put("accessToken", jwtDto.getAccessToken());
        maps.put("refreshToken", jwtDto.getRefreshToken());
        return ResponseEntity.ok(maps);
    }

    @ApiOperation(value = "C-SIGN-00", notes = "로그아웃.")
    @GetMapping("/logout")
    public ResponseEntity<?> userLogout(Authentication authentication,
        @Valid @RequestBody LogoutRequest logoutRequest) {
        return loginService.logout(logoutRequest, authentication);
    }

    @ApiOperation(value = "C-SIGN-09", notes = "accessToken 만료시에 다시 accessToken을 발급받습니다.")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@RequestBody ToReissueToken refreshToken) {
        try {
            String token = refreshToken.getRefreshToken();
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String rowBody = token.split("\\.")[1];
            String body = new String(decoder.decode(rowBody));
            JSONParser parser = new JSONParser();
            JSONObject jsonBody = (JSONObject) parser.parse(body);
            Long userId = (Long) jsonBody.get("sub");
            String accessToken = loginService.reissue(userId, refreshToken.getRefreshToken());
            HashMap<String, String> maps = new HashMap<>();
            maps.put("accessToken", accessToken);
            return new ResponseEntity<Object>(maps, HttpStatus.OK);
        } catch (ParseException e) {
            throw new UnauthorizedException("토큰이 유효하지 않습니다.");
        }
    }

    @ApiOperation(value = "C-SIGN-03", notes = "비밀번호 임시발급 인증코드 발급")
    @PostMapping("/find")
    public ResponseEntity<?> sendMailForTemporaryIssuingPassword(
        @Valid @RequestBody EmailAddress address) {
        String email = address.getEmail();

        if (!this.memberService.emailExistsCheck(email)) {
            throw new NotFoundException("가입되지 않은 이메일 입니다!");
        }

//        Message message = emailService.sendEmail(address.getEmail(),
//            "비밀번호 임시 발급을 위해 아래의 코드를 입력창에 입력해 주세요.\n\n%s\n");
        this.emailService.sendAuthCode(address.getEmail(),
            "비밀번호 임시 발급을 위해 아래의 코드를 입력창에 입력해 주세요.\n\n%s\n");
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "C-SIGN-03", notes = "비밀번호 임시발급 인증코드 확인")
    @PostMapping("/find-password")
    public ResponseEntity<?> temporaryIssuingPasswordCheck(@RequestBody EmailCode code) {
        this.emailService.emailCodeVerification(code.getEmail(), code.getCode());
        User user = this.memberService.getUserByEmail(code.getEmail());
        String randomPassword = this.memberService.getRandomPassword();
        this.memberService.changePassword(user, randomPassword);
        this.emailService.sendEmail(code.getEmail(), "Peer 임시 비밀번호",
            "임시 비밀번호입니다.\n\nrandomPassword");
        return ResponseEntity.ok().build();
    }
}
