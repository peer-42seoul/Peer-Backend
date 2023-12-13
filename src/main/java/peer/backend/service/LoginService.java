package peer.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.dto.security.response.JwtDto;
import peer.backend.entity.user.Admin;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.UnauthorizedException;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.blacklist.BlacklistService;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final AdminService adminService;
    private final BlacklistService blacklistService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public JwtDto login(String userEmail, String password) {
        // no username
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UnauthorizedException("Email 혹은 비밀번호가 잘못되었습니다!"));
        // wrong password
        if (!encoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Email 혹은 비밀번호가 잘못되었습니다!");
        }

        // 블랙리스트 체크
        if (blacklistService.isExistsByUserId(user.getId())) {
            throw new UnauthorizedException("정지된 계정입니다.");
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
    public ResponseEntity<?> logout(User user) {
        if (redisTemplate.opsForValue().get("refreshToken:" + user.getEmail()) != null) {
            redisTemplate.delete("refreshToken:" + user.getEmail());
        }
        //redis에 만료되지 않은 accessToken 추가
//        Long expiration = tokenProvider.getExpiration(logoutRequest.getAccessToken());
//        redisTemplate.opsForValue()
//            .set(logoutRequest.getAccessToken(), "unexpiredAccessToken", expiration,
//                TimeUnit.MILLISECONDS);
        return ResponseEntity.ok("logout success.");
    }

    public String reissue(Long userId, String refreshToken) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UnauthorizedException("올바르지 않은 accessToken/refreshToken 입니다."));
        String RefreshTokenInRedis = redisTemplate.opsForValue()
            .get("refreshToken:" + user.getEmail());
//        if (RefreshTokenInRedis == null) {
//            // redirect login Page (이건 프론트가 access랑 redir이 만료인지 체크해야한다?)
//        }
        if (!refreshToken.equals(RefreshTokenInRedis)) {
            throw new UnauthorizedException("올바르지 않은 accessToken/refreshToken 입니다.");
        }
        return tokenProvider.createAccessToken(user);
    }

    @Transactional
    public String adminLogin(String id, String password) {
        Admin admin = this.adminService.getAdminByAdminId(id);

        if (!admin.getPassword().equals(password)) {
            throw new UnauthorizedException("비밀번호가 틀렸습니다.");
        }

        return tokenProvider.createAccessToken(admin);
    }
}
