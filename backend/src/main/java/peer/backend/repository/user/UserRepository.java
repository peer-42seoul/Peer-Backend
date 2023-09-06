package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);

    Optional<User> findById(String id);
}
