package peer.backend.repository.achievement;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.achievement.Achievement;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

}
