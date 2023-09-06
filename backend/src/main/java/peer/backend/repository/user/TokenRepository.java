package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.RefreshToken;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
}
