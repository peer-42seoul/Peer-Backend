package peer.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import peer.backend.config.jwt.TokenProvider;
import peer.backend.entity.user.User;
import peer.backend.exception.ForbiddenException;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${jwt.token.secret}")
    private String secretKey;

    @Value("${jwt.token.validity-in-seconds}")
    private Long validityInSeconds;

    public String login(String userEmail, String password) {
        // no username
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ForbiddenException("No such user"));
        // wrong password
        if (!encoder.matches(password, user.getPassword())) {
            throw new ForbiddenException("Wrong password");
        }
        return TokenProvider.createToken(user, secretKey, validityInSeconds);
    }
}
