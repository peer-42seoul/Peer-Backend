package peer.backend.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import peer.backend.entity.user.User;
import peer.backend.repository.user.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@Log4j2
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        log.info("loadUserByUsername id = " + id);
        User user = userRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> {
                    log.info("유저를 찾을 수 없습니다.");
                    return new UsernameNotFoundException("유저를 찾을 수 없습니다.");
                });

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        return new org
                .springframework
                .security
                .core
                .userdetails
                .User(user.getEmail(), user.getPassword(), grantedAuthorities);
    }
}