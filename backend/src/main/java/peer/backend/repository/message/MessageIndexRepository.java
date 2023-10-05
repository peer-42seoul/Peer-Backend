package peer.backend.repository.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.message.MessageIndex;

import java.util.Optional;

public interface MessageIndexRepository extends JpaRepository<MessageIndex, Long> {
    @Query("SELECT m FROM MessageIndex m WHERE (m.user1.id = :id1 AND m.user2.id = :id2) OR (m.user1.id = :id2 AND m.user2.id = :id1)")
    Optional<MessageIndex> findByUserIdx(Long id1, Long id2);
}
