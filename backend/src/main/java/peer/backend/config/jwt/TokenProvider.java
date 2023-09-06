package peer.backend.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import peer.backend.entity.user.RefreshToken;
import peer.backend.entity.user.User;
import peer.backend.repository.user.RefreshTokenRepository;
import peer.backend.service.UserDetailsServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${jwt.token.secret}")
    private String secretKey;

    @Value("${jwt.token.validity-in-seconds}")
    private long accessExpirationTime;

    @Value("${jwt.token.validity-in-seconds-refresh}")
    private long refreshExpirationTime;

    public static boolean isExpired(String token, Key key) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    public String createAccessToken(User user) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.claims();
        claims.put("sub", user.getId());
        claims.put("role", "ROLE_USER");

        return Jwts.builder()
                .setHeaderParam("typ", "accessToken")
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
                ;
    }

    public String createRefreshToken(User user) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims = Jwts.claims();
        claims.put("sub", user.getId());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        String refreshToken = Jwts.builder()
                .setHeaderParam("typ", "refreshToken")
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        //refreshToken entity 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);
        return refreshToken;
    }

    /**
     * 토큰으로부터 클레임을 만들고, 이를 통해 User 객체 생성해 Authentication 객체 반환
     */
    public Authentication getAuthentication(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        String id = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        log.info("id = " + id);
        UserDetails userDetails = userDetailsService.loadUserByUsername(id);
        return new UsernamePasswordAuthenticationToken(userDetails, "",  userDetails.getAuthorities());
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
    public int validateToken(String token){
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        if (token == null) {
            return 1;
        }
        // 토큰 검증
        if (TokenProvider.isExpired(token, key)) {
            return 2;
        }
        return 0;
    }

    public boolean validateRefreshToken(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return isExpired(token, key);
    }
}
