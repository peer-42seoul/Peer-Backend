package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.tag.UserSkills;

public interface UserSkillsRepository extends JpaRepository<UserSkills, Long> {
}
