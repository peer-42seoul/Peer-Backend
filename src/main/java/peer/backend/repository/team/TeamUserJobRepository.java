package peer.backend.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.composite.TeamUserJobPK;
import peer.backend.entity.team.TeamUserJob;
import peer.backend.entity.team.enums.TeamUserStatus;

import java.util.List;

public interface TeamUserJobRepository extends JpaRepository<TeamUserJob, TeamUserJobPK> {

    @Query("SELECT tuj FROM TeamUserJob tuj WHERE tuj.teamUser.teamId = :teamId AND tuj.status = :status")
    public List<TeamUserJob> findByTeamUserTeamIdAndStatus(Long teamId, TeamUserStatus status);
}
