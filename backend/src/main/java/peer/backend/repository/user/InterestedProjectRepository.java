package peer.backend.repository.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.InterestedProject;
import peer.backend.entity.user.UserLink;

public interface InterestedProjectRepository extends JpaRepository<InterestedProject, Long> {

    void deleteByUserIdAndTeamId(Long userId, Long teamId);
}
