package peer.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import peer.backend.entity.user.User;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> checkUser = repository.findByName(username);
        if (checkUser.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User not exitst with name : %s", username));
        }
        return new PrincipalDetails(checkUser.get());
    }
}
