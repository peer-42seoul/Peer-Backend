package peer.backend.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import peer.backend.dto.privateinfo.InitSecretDTO;
import peer.backend.dto.privateinfo.InitTokenDTO;
import peer.backend.dto.privateinfo.MainSeedDTO;
import peer.backend.dto.privateinfo.PrivateDataDTO;
import peer.backend.dto.privateinfo.enums.PrivateActions;
import peer.backend.dto.profile.request.ChangePasswordRequest;
import peer.backend.dto.profile.request.PasswordRequest;
import peer.backend.dto.security.UserInfo;
import peer.backend.dto.security.request.UserLoginRequest;
import peer.backend.dto.security.response.JwtDto;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.ConflictException;
import peer.backend.exception.ForbiddenException;

import peer.backend.exception.IllegalArgumentException;
import peer.backend.service.profile.PersonalInfoService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PrivateInfoWrappingService {

    private final MemberService memberService;
    private final PersonalInfoService personalInfoService;
    private final RedisTemplate<Long, String> redisTemplateForInitKey;
    private final RedisTemplate<String, String> redisTemplateForSecret;
    private final LoginService loginService;

    @Value("${jwt.token.validity-in-seconds-refresh}")
    private long refreshExpirationTime;


    public PrivateInfoWrappingService(
            @Qualifier("redisTemplateForInitKey") RedisTemplate<Long, String> redisTemplateForInitKey,
            RedisTemplate<String, String> redisTemplate,
            MemberService memberService,
            PersonalInfoService personalInfoService, LoginService loginService) {
        this.redisTemplateForInitKey = redisTemplateForInitKey;
        this.redisTemplateForSecret = redisTemplate;
        this.memberService = memberService;
        this.personalInfoService = personalInfoService;
        this.loginService = loginService;
    }


    private boolean checkCodeUniqueOrNotForInit(Long key) {
        String value = this.redisTemplateForInitKey.opsForValue().get(key);
        return value == null;
    }

    private boolean checkCodeUniqueOrNotForToken(String key) {
        String value = this.redisTemplateForSecret.opsForValue().get(key);
        return value == null;
    }

    private void saveMainSeedToRedis(MainSeedDTO data) {
        String code = data.getCode();

        // 값 저장
        this.redisTemplateForSecret
                .opsForValue()
                .set(code, data.getSeed());
        // 5분 설정
        this.redisTemplateForSecret
                .expire(code, 5, TimeUnit.MINUTES);
    }

    private void saveInitSecretToRedis(InitSecretDTO value) {
        // 값 저장
        Long code = Long.parseLong(value.getCode());
        this.redisTemplateForInitKey
                .opsForValue()
                .set(code, value.getSecret());
        // 5분 설정
        this.redisTemplateForInitKey
                .expire(code, 5, TimeUnit.MINUTES);
    }

    private void saveCodeAndActionToRedis(Long code, PrivateActions act) {
        Long value = (long) act.getCode();
        // 값 저장
        this.redisTemplateForSecret
                .opsForValue()
                .set("act-" + code, value.toString());
        // 5분 설정
        this.redisTemplateForSecret
                .expire("act-" + code, 5, TimeUnit.MINUTES);
    }
    private Long makeInitCode() {
        SecureRandom codeMaker = new SecureRandom();

        long result = codeMaker.nextLong() & Long.MAX_VALUE;
        while(!checkCodeUniqueOrNotForInit(result)) {
            result = codeMaker.nextLong() & Long.MAX_VALUE;
        }
        return result;
    }
    public InitSecretDTO makeInitSecret() {
        //SecureRandom 난수 생성기
        SecureRandom random = new SecureRandom();

        // 256바이트 난수 생성을 위한 byte배열
        byte[] values = new byte[256];
        random.nextBytes(values);

        // 16진수 문자열로 변환
        StringBuilder sb = new StringBuilder();
        for(byte b : values) {
            sb.append(String.format("%02x", b));
        }

        // sb 를 secret 으로 활용하면 됨
        InitSecretDTO result = InitSecretDTO.builder()
                .secret(sb.toString())
                .code(this.makeInitCode().toString())
                .build();

        this.saveInitSecretToRedis(result);
        return result;
    }

    public MainSeedDTO parseInitToken(InitTokenDTO jwt) {
        String secret = this.redisTemplateForInitKey.opsForValue().get(Long.parseLong(jwt.getCode()));
        this.redisTemplateForInitKey.delete(Long.parseLong(jwt.getCode()));
        if (secret == null)
            throw new BadRequestException("비정상적인 접근입니다.");

        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        // 토큰의 클레임 가져오기
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key).build()
                        .parseClaimsJws(jwt.getToken())
                                .getBody();

        int apiType = claims.get("apiType", Integer.class);
        boolean success = false;
        MainSeedDTO result = null;

        for (PrivateActions act : PrivateActions.values()) {
            if (act.getCode() == apiType) {
                result = this.makeTokenAndKey(act);
                success = true;
            }
        }
        if (!success)
            throw new BadRequestException("비정상적인 접근입니다.");

        return result;
    }

    private Claims parseSecretData(PrivateDataDTO data) {
        String secret = this.redisTemplateForSecret
                .opsForValue().get(data.getCode());
        this.redisTemplateForSecret.delete(data.getCode());
        if (secret == null)
            throw new BadRequestException("비정상적인 접근입니다.");
        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(data.getToken())
                .getBody();
    }

    private UserInfo getDataForSignUP(PrivateDataDTO data) throws IllegalArgumentException {
        Claims target = this.parseSecretData(data);
  
        String email = target.get("email", String.class);
        String password = target.get("password", String.class);;
        String nickname = target.get("nickname", String.class);;
        String name = target.get("name", String.class);;
        String socialEmail = target.get("socialEmail", String.class);;
        return new UserInfo(email, password, nickname, name, socialEmail);
    }

    private UserLoginRequest getDataForSingIn(PrivateDataDTO data) throws IllegalArgumentException {
        Claims target = this.parseSecretData(data);

        String userEmail = target.get("userEmail", String.class);
        String password = target.get("password", String.class);

        return new UserLoginRequest(userEmail, password);
    }

    private PasswordRequest getDataForPasswordCheck(PrivateDataDTO data) throws IllegalArgumentException {
        Claims target = this.parseSecretData(data);
        String password = target.get("password", String.class);

        return new PasswordRequest(password);
    }

    private ChangePasswordRequest getDataForPasswordChange(PrivateDataDTO data) throws IllegalArgumentException {
        Claims target = this.parseSecretData(data);
        String password = target.get("password", String.class);
        String code = target.get("code", String.class);
        return new ChangePasswordRequest(password, code);
    }

    public ResponseEntity<?> processDataFromToken (User user, PrivateDataDTO data, HttpServletResponse response) {
        Integer type = Integer.parseInt(Objects.requireNonNull(this.redisTemplateForSecret.opsForValue().get("act-" + data.getCode())));
        this.redisTemplateForSecret.delete("act-" + data.getCode());

        if (type == PrivateActions.SIGNUP.getCode()){
            // 회원가입 폼 제출 로직
            UserInfo newUser;
            try {
                newUser = this.getDataForSignUP(data); }
            catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            this.memberService.signUp(newUser);
            return ResponseEntity.ok().build();

        } else if (type == PrivateActions.SIGNIN.getCode()) {
            UserLoginRequest userLoginRequest;
            try {
                userLoginRequest = this.getDataForSingIn(data);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
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

            return ResponseEntity.ok().body(maps);
        } else if (type == PrivateActions.PASSWORDCHECK.getCode()) {
            // 비밀번호 확인 로직
            PasswordRequest request;
            try {
                request = this.getDataForPasswordCheck(data);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            if (!this.memberService.verificationPassword(request.getPassword(), user.getPassword())){
                throw new ForbiddenException("비밀번호가 일치하지 않습니다!");
            }
            String uuid = this.personalInfoService.getChangePasswordCode(user.getId());
            HashMap<String, String> body = new HashMap<>();
            body.put("code", uuid);
            return  ResponseEntity.status(HttpStatus.CREATED).body(body);

        } else if (type == PrivateActions.PASSWORDMODIFY.getCode()) {
            // 비밀번호 변경 로직
            ChangePasswordRequest request;
            try {
                request = this.getDataForPasswordChange(data); }
            catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            if (!this.personalInfoService.checkChangePasswordCode(user.getId(), request.getCode())) {
                throw new ForbiddenException("유효하지 않은 코드입니다!");
            }
            if (this.memberService.verificationPassword(request.getPassword(), user.getPassword())) {
                throw new ConflictException("현재 비밀번호와 일치합니다!");
            }
            this.personalInfoService.changePassword(user, request.getPassword());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else  {
            throw new BadRequestException("비 정상적인 접근입니다.");
        }
    }

    private MainSeedDTO makeTokenAndKey(PrivateActions type) {
        SecureRandom randomMaker = new SecureRandom(type.getDescription().getBytes());

        // 256바이트 난수 생성을 위한 byte배열
        byte[] values = new byte[256];
        randomMaker.nextBytes(values);

        // 16진수 문자열로 변환
        StringBuilder sb = new StringBuilder();
        for(byte b : values) {
            sb.append(String.format("%02x", b));
        }

        // code 만들기
        Long result = randomMaker.nextLong() & Long.MAX_VALUE;
        while(!this.checkCodeUniqueOrNotForToken(result.toString())) {
            result = randomMaker.nextLong() & Long.MAX_VALUE;
        }

        // code, act 기억
        this.saveCodeAndActionToRedis(result, type);

        MainSeedDTO data = MainSeedDTO.builder()
                .seed(sb.toString())
                .code(result.toString())
                .build();
        this.saveMainSeedToRedis(data);

        return data;
    }
}

