package peer.backend.repository.user;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("SELECT m FROM User m WHERE (m.nickname LIKE %:keyword% AND m.activated = true) ORDER BY m.nickname")
    Optional<List<User>> findByKeyWord(String keyword);

    Optional<List<User>> findByEmailOrNickname(String email, String nickname);

    boolean existsByEmail(String email);

    @Query(value = "SELECT u FROM User u WHERE u.nickname LIKE %:keyword%", countQuery = "SELECT count(u) FROM User u")
    Page<User> findByNicknameContainingFromPageable(Pageable pageable, String keyword);

    List<User> findByIdIn(List<Long> ids);

    Long countByCreatedAtBefore(LocalDateTime time);
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM User n WHERE n.nickname = :nickname")
    Boolean existsByNickname(String nickname);

    @Query("UPDATE User m SET m.alarmCounter = m.alarmCounter + 1")
    void increaseAlarmCountForALL();

    @Query("UPDATE User m SET m.alarmCounter = CASE WHEN (m.alarmCounter - 1) < 0 then 0 ELSE (m.alarmCounter - 1) END")
    void decreaseAlarmCountForALL();

    @Query("UPDATE User m SET m.alarmCounter = m.alarmCounter + 1 WHERE m.id IN : userIds AND m.activated = true")
    void increaseAlarmCountForUsers(@Param("userIds") List<Long> userIds);

    @Modifying
    @Query("UPDATE User m SET m.alarmCounter = m.alarmCounter + 1, m.newAlarmCounter = m.newAlarmCounter + 1 WHERE m.id = :userId AND m.activated = true")
    void increaseAlarmCountForOne(@Param("userId") Long userId);

    @Query("UPDATE User m SET m.alarmCounter = CASE WHEN (m.alarmCounter - 1) < 0 then 0 ELSE (m.alarmCounter - 1) END WHERE m.id IN :userIds")
    void decreaseAlarmCountForUsers(@Param("userIds") List<Long> userIds);

    @Query("UPDATE User m SET m.alarmCounter = CASE WHEN (m.alarmCounter - 1) < 0 then 0 ELSE (m.alarmCounter - 1) END WHERE m.id = :userId")
    void decreaseAlarmCountForOne(@Param("userId") Long userId);

    @Query("SELECT m.id FROM User m")
    List<Long> findAllIds();
}
