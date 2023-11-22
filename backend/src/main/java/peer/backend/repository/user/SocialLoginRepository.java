package peer.backend.repository.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.SocialLogin;

public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {

    Optional<SocialLogin> findByEmail(String email);

    boolean existsByEmail(String email);
    List<SocialLogin> findAllByUserId(Long userId);
}
