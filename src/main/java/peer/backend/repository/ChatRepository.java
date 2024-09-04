package peer.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.chat.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
