package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
