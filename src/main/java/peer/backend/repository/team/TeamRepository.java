package peer.backend.repository.team;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.team.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);
}
