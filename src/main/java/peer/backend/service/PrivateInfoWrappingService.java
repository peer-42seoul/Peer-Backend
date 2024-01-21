package peer.backend.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import peer.backend.dto.privateinfo.InitSecretDTO;
import peer.backend.dto.privateinfo.InitTokenDTO;
import peer.backend.dto.privateinfo.MainSeedDTO;
import peer.backend.dto.privateinfo.PrivateDataDTO;
import peer.backend.dto.privateinfo.enums.PrivateActions;
import peer.backend.dto.profile.request.ChangePasswordRequest;
import peer.backend.dto.profile.request.PasswordRequest;
import peer.backend.dto.security.UserInfo;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.ConflictException;
import peer.backend.exception.ForbiddenException;
import peer.backend.service.profile.PersonalInfoService;

import javax.validation.Validator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.security.Key;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PrivateInfoWrappingService {

    private final MemberService memberService;
    private final PersonalInfoService personalInfoService;
    private final RedisTemplate<Long, String> redisTemplateForInitKey;
    private final RedisTemplate<String, String> redisTemplateForSecret;
    private final Validator validator;

    public PrivateInfoWrappingService(
            @Qualifier("redisTemplateForInitKey") RedisTemplate<Long, String> redisTemplateForInitKey,
            RedisTemplate<String, String> redisTemplate,
            MemberService memberService,
            PersonalInfoService personalInfoService,
            Validator validator) {
        this.redisTemplateForInitKey = redisTemplateForInitKey;
        this.redisTemplateForSecret = redisTemplate;
        this.memberService = memberService;
        this.personalInfoService = personalInfoService;
        this.validator = validator;
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
        // 값 저장
        this.redisTemplateForSecret
                .opsForValue()
                .set(data.getCode().toString(), data.getSeed());
        // 5분 설정
        this.redisTemplateForSecret
                .expire(data.getCode().toString(), 5, TimeUnit.MINUTES);
    }

    private void saveInitSecretToRedis(InitSecretDTO value) {
        // 값 저장
        this.redisTemplateForInitKey
                .opsForValue()
                .set(value.getCode(), value.getSecret());
        // 5분 설정
        this.redisTemplateForInitKey
                .expire(value.getCode(), 5, TimeUnit.MINUTES);
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
                .code(this.makeInitCode())
                .build();

        this.saveInitSecretToRedis(result);
        return result;
    }

    public MainSeedDTO parseInitToken(InitTokenDTO jwt) {
        String secret = this.redisTemplateForInitKey.opsForValue().get(jwt.getCode());
        this.redisTemplateForInitKey.delete(jwt.getCode());
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
                System.out.println("성공적으로 조건을 발견하였습니다. : " + act.getDescription());
                success = true;
            }
        }
        if (!success)
            throw new BadRequestException("비정상적인 접근입니다.");

        return result;
    }

    private Claims parseSecretData(PrivateDataDTO data) {
        String secret = this.redisTemplateForSecret
                .opsForValue().get(data.getCode().toString());
        this.redisTemplateForSecret.delete(data.getCode().toString());
        if (secret == null)
            throw new BadRequestException("비정상적인 접근입니다.");
        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(data.getToken())
                .getBody();
    }

    private UserInfo getDataForSignUP(PrivateDataDTO data) {
        Claims target = this.parseSecretData(data);

        @NotBlank(message = "이메일은 필수 항목입니다.")
        @Email(message = "이메일형식에 맞지 않습니다.")
        String email = target.get("email", String.class);
        String password = target.get("password", String.class);;
        String nickname = target.get("nickname", String.class);;
        String name = target.get("name", String.class);;
        String socialEmail = target.get("socialEmail", String.class);;

        return new UserInfo(email, password, nickname, name, socialEmail);
    }

    private PasswordRequest getDataForPasswordCheck(PrivateDataDTO data) {
        Claims target = this.parseSecretData(data);
        String password = target.get("password", String.class);

        return PasswordRequest.builder()
                .password(password)
                .build();
    }

    private ChangePasswordRequest getDataForPasswordChange(PrivateDataDTO data) {
        Claims target = this.parseSecretData(data);
        String password = target.get("password", String.class);
        String code = target.get("code", String.class);
        return ChangePasswordRequest.builder()
                .password(password)
                .code(code)
                .build();
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
                .code(result)
                .build();
        this.saveMainSeedToRedis(data);

        return data;
    }

    public ResponseEntity<?> processDataFromToken (User user, PrivateDataDTO data) {
        Integer type = Integer.parseInt(Objects.requireNonNull(this.redisTemplateForSecret.opsForValue().get("act-" + data.getCode())));
        this.redisTemplateForSecret.delete("act-" + data.getCode());

        if (type == PrivateActions.SIGNUP.getCode()){
            // 회원가입 폼 제출 로직
            System.out.println("여기로 들어왔음!!");
            UserInfo newUser = this.getDataForSignUP(data);
            this.memberService.signUp(newUser);
            return ResponseEntity.ok().build();

        } else if (type == PrivateActions.PASSWORDCHECK.getCode()) {
            // 비밀번호 확인 로직
            System.out.println("여기로 들어왔음!! 2");
            PasswordRequest request = this.getDataForPasswordCheck(data);
            if (!this.memberService.verificationPassword(request.getPassword(), user.getPassword())){
                throw new ForbiddenException("비밀번호가 일치하지 않습니다!");
            }
            String uuid = this.personalInfoService.getChangePasswordCode(user.getId());
            HashMap<String, String> body = new HashMap<>();
            body.put("code", uuid);
            return  ResponseEntity.status(HttpStatus.CREATED).body(body);

        } else if (type == PrivateActions.PASSWORDMODIFY.getCode()) {
            // 비밀번호 변경 로직
            System.out.println("여기로 들어왔음!! 3");
            ChangePasswordRequest request = this.getDataForPasswordChange(data);

            if (!this.personalInfoService.checkChangePasswordCode(user.getId(), request.getCode())) {
                throw new ForbiddenException("유효하지 않은 코드입니다!");
            }
            if (this.memberService.verificationPassword(request.getPassword(), user.getPassword())) {
                throw new ConflictException("현재 비밀번호와 일치합니다!");
            }
            this.personalInfoService.changePassword(user, request.getPassword());

        } else  {
            throw new BadRequestException("비 정상적인 접근입니다.");
        }
        return ResponseEntity.badRequest().build();
    }
}

