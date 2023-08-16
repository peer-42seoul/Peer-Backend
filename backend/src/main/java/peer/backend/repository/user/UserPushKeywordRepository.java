package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.UserPushKeyword;

public interface UserPushKeywordRepository extends JpaRepository<UserPushKeyword, Long> {

}
