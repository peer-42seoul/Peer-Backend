package peer.backend.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.peerOperation WHERE u.nickname = :nickname")
    Optional<User> findByNickname(String nickname);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.peerOperation WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.peerOperation WHERE u.id = :id")
    Optional<User> findById(Long id);

    @Query("SELECT m FROM User m WHERE (m.nickname LIKE %:keyword%) ORDER BY m.nickname")
    Optional<List<User>> findByKeyWord(String keyword);

    Optional<List<User>> findByEmailOrNickname(String email, String nickname);

    boolean existsByEmail(String email);

    @Query(value = "SELECT u FROM User u WHERE u.nickname LIKE %:keyword%", countQuery = "SELECT count(u) FROM User u")
    Page<User> findByNicknameContainingFromPageable(Pageable pageable, String keyword);

    List<User> findByIdIn(List<Long> ids);

    Long countByCreatedAtBefore(LocalDateTime time);
}
