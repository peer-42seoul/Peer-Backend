package peer.backend.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.team.TeamJob;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface TeamJobRepository extends JpaRepository<TeamJob, Long> {
    Optional<TeamJob> findByTeamIdAndName(Long teamId, String name);
    Optional<TeamJob> findByName(String name);
}
