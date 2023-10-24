package peer.backend.repository.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.message.MessagePiece;

import java.util.List;
import java.util.Optional;

public interface MessagePieceRepository extends JpaRepository<MessagePiece, Long> {
    @Query("SELECT m FROM MessagePiece m WHERE (m.targetConversationId =:conversationId) ORDER BY m.createdAt DESC")
    Optional<MessagePiece> findTopByConversationId(long conversationId);

    Optional<List<MessagePiece>> findByTargetConversationId(long conversationId);
}
