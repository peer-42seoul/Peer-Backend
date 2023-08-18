package peer.backend.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.team.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
