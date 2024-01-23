package peer.backend.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import peer.backend.dto.privateinfo.InitSecretDTO;
import peer.backend.dto.privateinfo.InitTokenDTO;
import peer.backend.dto.privateinfo.MainSeedDTO;
import peer.backend.dto.privateinfo.enums.PrivateActions;
import peer.backend.exception.BadRequestException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PrivateInfoWrappingService {

    private final RedisTemplate<Long, String> redisTemplateForInitKey;
    private final RedisTemplate<String, String> redisTemplateForSecret;

    public PrivateInfoWrappingService(
            @Qualifier("redisTemplateForInitKey") RedisTemplate<Long, String> redisTemplateForInitKey,
            RedisTemplate<String, String> redisTemplate) {
        this.redisTemplateForInitKey = redisTemplateForInitKey;
        this.redisTemplateForSecret = redisTemplate;
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
                .set(data.getCode(), data.getSeed());
        // 5분 설정
        this.redisTemplateForSecret
                .expire(data.getCode(), 5, TimeUnit.MINUTES);
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
        long result = randomMaker.nextLong() & Long.MAX_VALUE;
        while(!this.checkCodeUniqueOrNotForToken(type.getDescription()
                .getBytes(StandardCharsets.UTF_8)
                .toString() + "_" + result)) {
            result = randomMaker.nextLong() & Long.MAX_VALUE;
        }

        MainSeedDTO data = MainSeedDTO.builder()
                .seed(sb.toString())
                .code(type.getDescription()
                        .getBytes(StandardCharsets.UTF_8)
                        .toString() + "_" + result)
                .build();
        this.saveMainSeedToRedis(data);

        return data;
    }

}

