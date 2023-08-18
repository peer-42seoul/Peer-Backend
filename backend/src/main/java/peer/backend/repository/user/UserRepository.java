package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByNickname(String nickname);
    public Optional<User> findByEmail(String email);
    public Optional<User> findByName(String name);
}
