package peer.backend.repository.user;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.entity.tag.UserSkill;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM UserSkill m WHERE m.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}
