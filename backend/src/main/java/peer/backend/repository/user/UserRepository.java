package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);

    Optional<User> findById(Long id);

    @Query("SELECT m FROM User m WHERE (m.email LIKE %:keyword% OR m.nickname LIKE %:keyword%) ORDER BY m.nickname")
    Optional<List<User>> findByKeyWord(String keyword);
}
