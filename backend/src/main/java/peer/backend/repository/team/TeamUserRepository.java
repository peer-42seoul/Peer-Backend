package peer.backend.repository.team;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.user.InterestedProject;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    public TeamUser findByUserIdAndTeamId(Long userId, Long teamId);

    public void deleteByUserIdAndTeamId(Long userId, Long teamId);

    public List<TeamUser> findByUserId(Long userId);

}
