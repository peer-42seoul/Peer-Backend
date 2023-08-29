package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.UserLink;

public interface UserLinkRepository extends JpaRepository<UserLink, Long> {

}
