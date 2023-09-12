package peer.backend.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import peer.backend.entity.user.User;
import peer.backend.service.UserDetailsServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private final UserDetailsServiceImpl userDetailsService;

    @Value("${jwt.token.secret}")
    private String secretKey;

    @Value("${jwt.token.validity-in-seconds}")
    private long accessExpirationTime;

    @Value("${jwt.token.validity-in-seconds-refresh}")
    private long refreshExpirationTime;

    private Key key;
    private final RedisTemplate<String, String> redisTemplate;

    public String createAccessToken(User user) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.claims();
        claims.put("sub", user.getId());
        claims.put("role", "ROLE_USER");

        return Jwts.builder()
                .setHeaderParam("typ", "accessToken")
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact()
                ;
    }

    public String createRefreshToken(User user) {
        Claims claims = Jwts.claims();
        claims.put("sub", user.getId());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return  Jwts.builder()
                .setHeaderParam("typ", "refreshToken")
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void putRefreshTokenInRedis(User user, String refreshToken) {
        redisTemplate.opsForValue().set("refreshToken:" + user.getEmail(), refreshToken, refreshExpirationTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 토큰으로부터 클레임을 만들고, 이를 통해 User 객체 생성해 Authentication 객체 반환
     */
    public Authentication getAuthentication(String token) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        String id = Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(id);
        return new UsernamePasswordAuthenticationToken(userDetails, null,  userDetails.getAuthorities());
    }

    /**
     * http 헤더로부터 bearer 토큰을 가져옴.
     */
    public String resolveAccessToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Access 토큰을 검증
     */
    public boolean validateToken(String accessToken) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        if(ObjectUtils.isEmpty(redisTemplate.opsForValue().get(accessToken))) {
            // 기존의 인증 인증 로직
            return Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(accessToken).getBody().getExpiration().before(new Date());
        }
        return true;
    }

    public boolean validRefreshToken(String refreshToken) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(refreshToken).getBody().getExpiration().before(new Date());
    }


    public Long getExpiration(String accessToken) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(accessToken).getBody().getExpiration();
        return (expiration.getTime() - new Date().getTime());
    }
}
