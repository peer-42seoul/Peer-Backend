package peer.backend.repository.message;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.message.MessagePiece;

import java.util.List;

public interface MessagePieceRepository extends JpaRepository<MessagePiece, Long> {
    List<MessagePiece> findByConversationId(long conversationId);
}
