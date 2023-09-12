package peer.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.dto.security.request.LogoutRequest;
import peer.backend.dto.security.response.JwtDto;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.UnauthorizedException;
import peer.backend.repository.user.UserRepository;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public JwtDto login(String userEmail, String password) {
        // no username
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UnauthorizedException("로그인이 실패"));
        // wrong password
        if (!encoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("로그인이 실패");
        }
        // create jwtDto
        JwtDto jwtDto = new JwtDto(
                user.getId(),
                tokenProvider.createAccessToken(user),
                tokenProvider.createRefreshToken(user)
        );
        try {
            tokenProvider.putRefreshTokenInRedis(user, jwtDto.getRefreshToken());
        } catch (Exception e) {
            throw new BadRequestException("잘못된 요청입니다.");
        }
        return jwtDto;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> logout(LogoutRequest logoutRequest, Authentication authentication) {
        try {
            if (tokenProvider.validateToken(logoutRequest.getAccessToken())) {
                throw new BadRequestException("잘못된 토큰으로 로그아웃을 시도했습니다.");
            }
        } catch (Exception e) {
            throw new BadRequestException("잘못된 토큰으로 로그아웃을 시도했습니다.");
        }
        if (redisTemplate.opsForValue().get("refreshToken:" + authentication.getName()) != null) {
            redisTemplate.delete("refreshToken:" + authentication.getName());
        }
        //redis에 만료되지 않은 accessToken 추가
        Long expiration = tokenProvider.getExpiration(logoutRequest.getAccessToken());
        redisTemplate.opsForValue().set(logoutRequest.getAccessToken(), "unexpiredAccessToken", expiration, TimeUnit.MILLISECONDS);
        return ResponseEntity.ok("logout success.");
    }

    public String reissue(Long userId, String refreshToken) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UnauthorizedException("올바르지 않은 accessToken/refreshToken 입니다."));
        String RefreshTokenInRedis = redisTemplate.opsForValue().get("refreshToken:" + user.getEmail());
//        if (RefreshTokenInRedis == null) {
//            // redirect login Page (이건 프론트가 access랑 redir이 만료인지 체크해야한다?)
//        }
        if (!refreshToken.equals(RefreshTokenInRedis)) {
            throw new UnauthorizedException("올바르지 않은 accessToken/refreshToken 입니다.");
        }
        return tokenProvider.createAccessToken(user);
    }
}
