package peer.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.dto.security.response.JwtDto;
import peer.backend.entity.user.User;
import peer.backend.exception.ForbiddenException;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
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
}
