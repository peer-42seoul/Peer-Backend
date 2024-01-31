package peer.backend.repository.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.message.MessageIndex;

import java.util.List;
import java.util.Optional;

public interface MessageIndexRepository extends JpaRepository<MessageIndex, Long> {
    @Query("SELECT m FROM MessageIndex m WHERE (m.userIdx1 = :id1 AND m.userIdx2 = :id2 AND m.user1delete = false AND m.user2delete = false) OR (m.userIdx1 = :id2 AND m.userIdx2 = :id1 AND m.user1delete = false AND m.user2delete = false)")
    Optional<MessageIndex> findByUserIdx(Long id1, Long id2);

    @Query("SELECT m FROM MessageIndex m WHERE (m.userIdx1 = :id OR m.userIdx2 = :id)")
    Optional<List<MessageIndex>> findByUserId(long id);

    Optional<MessageIndex> findTopByConversationId(long conversationId);

    @Query("SELECT m FROM MessageIndex m WHERE " +
            "((m.userIdx1 = : userId1 AND m.userIdx2 = :userId2)" +
            " OR " +
            "(m.userIdx1 = :userId2 AND m.userIdx2 = :userId1))" +
            " AND " +
            "(m.user1delete = false AND m.user2delete = false)")
    Optional<MessageIndex> findByUserIdx1AndUserIdx2(Long userId1, Long userId2);
}
