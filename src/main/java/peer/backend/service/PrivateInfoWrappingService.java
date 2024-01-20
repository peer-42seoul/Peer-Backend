package peer.backend.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import peer.backend.dto.privateInfo.InitSecretDTO;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PrivateInfoWrappingService {
    private static final String SECRET_KEY= "8f9142dcc8bf95b3cedde05dedc01da51290008d7803db3619408ee58b2432b6b435c5b9190f20df2e787ad38283f3f2960751b4b8a0f60289dfb2b0d046bb363aff15e68c5e1f8835d58e3e8138c967665f858723ae169940df067abe7aa52fdf20c58df63e10a2222e1141bd9af4fd9a16a3880857e4166c8f4854ed096f132c1fc4e4ac1c7e48281f829fbc3459809d83e8e8fad2feae0e221a38ced81a235a66ca31a18f58b44a97434c528b85418f815a2f1fca3816ff206aca594b06ff9453ad26075ee64fb9a1d731ff120e3c2ce7f62cf33ad0776d6c6e779596908a8bca9cb0a242e5d37a7685140d4a7969c93873913ff69aee7639974b5dd7563d";

    private final RedisTemplate<Long, String> redisTemplateForInitKey;
    private final RedisTemplate<String, String> redisTemplateForSecret;

    public PrivateInfoWrappingService(
            @Qualifier("redisTemplateForInitKey") RedisTemplate<Long, String> redisTemplateForInitKey,
            RedisTemplate<String, String> redisTemplate) {
        this.redisTemplateForInitKey = redisTemplateForInitKey;
        this.redisTemplateForSecret = redisTemplate;
    }


    private boolean checkCodeUniqueOrNot(Long key) {
        String value = this.redisTemplateForInitKey.opsForValue().get(key);
        return value == null;
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
        Random codeMaker = new Random();

        long result = codeMaker.nextLong() & Long.MAX_VALUE;
        while(!checkCodeUniqueOrNot(result)) {
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

        System.out.println("만들어진 key " + result.getSecret());
        System.out.println("만들어진 code " + result.getCode());

        this.saveInitSecretToRedis(result);
        return result;
    }

    public Claims parseInitToken(String jwt) {
        // 시크릿 키를 바이트 배열로 변환
//        byte[] secretKeyBytes = Base64.getEncoder().encode(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
//        byte[] secretKeyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
//        System.out.println(secretKeyBytes);

        // 시크릿 키로부터 서명 키 생성
//        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        System.out.println("Key spec: " + key.getAlgorithm());
        System.out.println("Key spec: " + key.getFormat());
        System.out.println("Key spec: " + key.getEncoded());


        System.out.println("token : " + jwt);
        // 토큰의 클레임 가져오기
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key).build()
                        .parseClaimsJws(jwt)
                                .getBody();


        // 클레임 출력
        System.out.println("Issuer: " + claims.getIssuer());
        System.out.println("Subject: " + claims.getSubject());
        System.out.println("apiType: " + claims.get("apiType", Integer.class));
        return claims;
    }
}
