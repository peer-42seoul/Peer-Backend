package peer.backend.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import peer.backend.entity.user.Login;
import peer.backend.entity.user.enums.Role;
import peer.backend.exception.UnauthorizedException;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.user.AdminRepository;
import peer.backend.repository.user.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public UserDetails loadUserByUsername(String id, Role role) throws UsernameNotFoundException {
        Login user;

        if (Objects.isNull(role)) {
            throw new UnauthorizedException("잘못된 Access Token 입니다!");
        } else if (role.equals(Role.ROLE_USER)) {
            user = this.userRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
        } else {
            user = this.adminRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new UsernameNotFoundException("어드민을 찾을 수 없습니다."));
        }

        return new PrincipalDetails(user);
    }
}