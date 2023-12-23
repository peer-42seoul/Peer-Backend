package peer.backend.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import peer.backend.entity.user.Login;
import peer.backend.entity.user.User;
import peer.backend.entity.user.enums.Role;
import peer.backend.service.PrincipalDetailsService;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private final PrincipalDetailsService principalDetailsService;

    @Value("${jwt.token.secret}")
    private String secretKey;

    @Value("${jwt.token.validity-in-seconds}")
    private long accessExpirationTime;

    @Value("${jwt.token.validity-in-seconds-refresh}")
    private long refreshExpirationTime;

    private Key key;
    private final RedisTemplate<String, String> redisTemplate;

    public String createAccessToken(Login user) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.claims();
        claims.put("sub", user.getId());
        claims.put("role", user.getRole());

        String ret = Jwts.builder()
            .setHeaderParam("typ", "accessToken")
            .setClaims(claims)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
            .signWith(this.key, SignatureAlgorithm.HS256)
            .compact();
//        String redisKey = "redis_" + user.getId().toString();
//        redisTemplate.opsForValue().set(redisKey, ret);
        return ret;
    }

    public String createRefreshToken(Login user) {
        Claims claims = Jwts.claims();
        claims.put("sub", user.getId());
        claims.put("role", user.getRole());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());

        return Jwts.builder()
            .setHeaderParam("typ", "refreshToken")
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expireDate)
            .signWith(this.key, SignatureAlgorithm.HS256)
            .compact();
    }

    public void putRefreshTokenInRedis(User user, String refreshToken) {
        redisTemplate.opsForValue()
            .set("refreshToken:" + user.getEmail(), refreshToken, refreshExpirationTime,
                TimeUnit.MILLISECONDS);
    }

    /**
     * 토큰으로부터 클레임을 만들고, 이를 통해 User 객체 생성해 Authentication 객체 반환
     */
    public Authentication getAuthentication(String token) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(token)
            .getBody();
        String id = claims.getSubject();
        Role role = Role.from(String.valueOf(claims.get("role")));
        UserDetails userDetails = principalDetailsService.loadUserByUsername(id, role);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }

    /**
     * http 헤더로부터 bearer 토큰을 가져옴.
     */
    public String resolveAccessToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        bearerToken = req.getParameter("accessToken");
        return bearerToken;
    }

    public boolean validateToken(String accessToken) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        try {
            boolean val = Jwts.parserBuilder().setSigningKey(this.key).build()
                .parseClaimsJws(accessToken).getBody().getExpiration().after(new Date());
            Date exp = Jwts.parserBuilder().setSigningKey(this.key).build()
                .parseClaimsJws(accessToken).getBody().getExpiration();
//            log.info("exp : " + exp);
//            log.info("system 시간 : " + new Date(System.currentTimeMillis()));
//            log.info("걍 시간 : " + new Date());
            return val;
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean validRefreshToken(String refreshToken) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        try {
            return Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(refreshToken)
                .getBody().getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }


    public Long getExpiration(String accessToken) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder().setSigningKey(this.key).build()
            .parseClaimsJws(accessToken).getBody().getExpiration();
        return (expiration.getTime() - new Date().getTime());
    }
}
