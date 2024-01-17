package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.tag.UserSkill;

public interface UserSkillsRepository extends JpaRepository<UserSkill, Long> {
}
