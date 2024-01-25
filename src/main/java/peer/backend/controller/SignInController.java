package peer.backend.controller;

import io.swagger.annotations.ApiOperation;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.security.request.AdminLoginRequest;
import peer.backend.dto.security.request.EmailAddress;
import peer.backend.dto.security.request.EmailCode;
import peer.backend.dto.security.request.UserLoginRequest;
import peer.backend.dto.security.response.JwtDto;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.exception.UnauthorizedException;
import peer.backend.service.EmailAuthService;
import peer.backend.service.LoginService;
import peer.backend.service.MemberService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/signin")
public class SignInController {

    private final LoginService loginService;
    private final MemberService memberService;
    private final EmailAuthService emailService;

    @Value("${jwt.token.validity-in-seconds-refresh}")
    private long refreshExpirationTime;

    @Value("${jwt.token.validity-in-seconds}")
    private long accessExpirationTime;

    private static final String DEV_DOMAIN_URL = "peer-test.co.kr";

    @ApiOperation(value = "C-SIGN-01", notes = "로그인.")
    @PostMapping()
    public ResponseEntity<Object> login(@Valid @RequestBody UserLoginRequest userLoginRequest,
        HttpServletResponse response) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        JwtDto jwtDto = loginService.login(userLoginRequest.getUserEmail(),
            userLoginRequest.getPassword());
        maps.put("isLogin", "true");
        maps.put("userId", jwtDto.getUserId());
        maps.put("accessToken", jwtDto.getAccessToken());
        Cookie cookie = new Cookie("refreshToken", jwtDto.getRefreshToken());
        cookie.setMaxAge((int) refreshExpirationTime / 1000);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);

        response.addCookie(cookie);

        return ResponseEntity.ok()
            .body(maps);
    }

    @ApiOperation(value = "C-SIGN-09", notes = "accessToken 만료시에 다시 accessToken을 발급받습니다.")
    @GetMapping("/reissue")
    public ResponseEntity<?> reissueToken(@CookieValue("refreshToken") String refreshToken) {
        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String rowBody = refreshToken.split("\\.")[1];
            String body = new String(decoder.decode(rowBody));
            JSONParser parser = new JSONParser();
            JSONObject jsonBody = (JSONObject) parser.parse(body);
            Long userId = (Long) jsonBody.get("sub");
            String accessToken = loginService.reissue(userId, refreshToken);
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
            "임시 비밀번호입니다.\n\n" + randomPassword + "\n");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin")
    public ResponseEntity<Object> adminLogin(
        @RequestBody @Valid AdminLoginRequest adminLoginRequest, HttpServletResponse response) {
        String accessToken = loginService.adminLogin(adminLoginRequest.getId(),
            adminLoginRequest.getPassword());

        Cookie cookie = new Cookie("adminToken", accessToken);
        cookie.setMaxAge((int) accessExpirationTime / 1000);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
//        cookie.setSecure(true);

        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}
