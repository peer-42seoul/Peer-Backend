package peer.backend.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.team.TeamJob;

import java.util.Optional;

public interface TeamJobRepository extends JpaRepository<TeamJob, Long> {
    Optional<TeamJob> findByName(String name);
}
