package peer.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.chat.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByTeamId(Long teamId);
}
