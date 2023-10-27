package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.UserLink;

import java.util.List;

public interface UserLinkRepository extends JpaRepository<UserLink, Long> {
    List<UserLink> findAllByUserId(Long userId);
}
