package peer.backend.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import peer.backend.entity.user.Login;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.user.AdminRepository;
import peer.backend.repository.user.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Login user = this.userRepository.findById(Long.parseLong(id)).orElse(null);
        if (Objects.isNull(user)) {
            user = this.adminRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
        }

        return new PrincipalDetails(user);
    }
}