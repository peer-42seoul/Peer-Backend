package peer.backend.repository.message;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.message.Message;
import peer.backend.entity.user.User;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrReceiver(User sender, User receiver);
}
