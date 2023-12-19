package peer.backend.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.composite.TeamUserJobPK;
import peer.backend.entity.team.TeamUserJob;

public interface TeamUserJobRepository extends JpaRepository<TeamUserJob, TeamUserJobPK> {
}
