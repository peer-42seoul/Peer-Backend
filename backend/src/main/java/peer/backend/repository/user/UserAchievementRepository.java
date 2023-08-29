package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.UserAchievement;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

}
