package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.InterestedProject;
import peer.backend.entity.user.UserLink;

public interface InterestedProjectRepository extends JpaRepository<InterestedProject, Long> {

    public InterestedProject findByUserIdAndTeamId(Long userId, Long teamId);

    public void deleteByUserIdAndTeamId(Long userId, Long teamId);
}
