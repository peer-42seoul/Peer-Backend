package peer.backend.repository.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.message.MessagePiece;

import java.util.List;
import java.util.Optional;

public interface MessagePieceRepository extends JpaRepository<MessagePiece, Long> {
    Page<MessagePiece> findTopByTargetConversationIdOrderByCreatedAtDesc(long conversationId, Pageable pageable);

    Optional<List<MessagePiece>> findByTargetConversationId(long conversationId);
}
