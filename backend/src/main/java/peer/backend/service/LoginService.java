package peer.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.dto.security.Message;
import peer.backend.dto.security.response.ErrorDto;
import peer.backend.dto.security.response.JwtDto;
import peer.backend.entity.user.RefreshToken;
import peer.backend.entity.user.User;
import peer.backend.exception.ForbiddenException;
import peer.backend.repository.user.TokenRepository;
import peer.backend.repository.user.UserRepository;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public JwtDto login(String userEmail, String password) {
        // no username
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ForbiddenException("No such user"));
        // wrong password
        if (!encoder.matches(password, user.getPassword())) {
            throw new ForbiddenException("Wrong password");
        }

        log.info("user = " + user);
        // create jwtDto
        return new JwtDto(
                tokenProvider.createAccessToken(user),
                tokenProvider.createRefreshToken(user)
                );
    }

    public Message reissue(Long userId, String refreshToken) {
        Optional<RefreshToken> optionalToken = tokenRepository.findById(userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalToken.isEmpty() || optionalUser.isEmpty()) {
            return new Message(HttpStatus.UNAUTHORIZED, "올바르지 않은 accessToken/refreshToekn입니다.", "/accesstoken");
        }
        RefreshToken token = optionalToken.get();
        if (tokenProvider.validateRefreshToken(refreshToken)) {
            return new Message(HttpStatus.UNAUTHORIZED, "refreshToken이 만료되었습니다.", "/accesstoken");
        } else if (!refreshToken.equals(token.getRefreshToken())) {
            return new Message(HttpStatus.UNAUTHORIZED, "올바르지 않은 accessToken/refreshToekn입니다.", "/accesstoken");
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("accessToken", tokenProvider.createAccessToken(optionalUser.get()));
        return new Message(HttpStatus.OK, result);
    }
}
