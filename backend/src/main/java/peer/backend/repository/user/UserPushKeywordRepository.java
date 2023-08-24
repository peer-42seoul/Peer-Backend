package peer.backend.repository.user;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.UserPushKeyword;

public interface UserPushKeywordRepository extends JpaRepository<UserPushKeyword, Long> {

    List<UserPushKeyword> findAllByUserId(Long userId);

    void deleteByUserIdAndKeyword(Long userId, String keyword);
}
