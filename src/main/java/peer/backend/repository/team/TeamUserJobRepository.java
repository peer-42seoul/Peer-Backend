package peer.backend.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.composite.TeamUserJobPK;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUserJob;
import peer.backend.entity.team.enums.TeamUserStatus;

import java.util.List;
import java.util.Optional;

public interface TeamUserJobRepository extends JpaRepository<TeamUserJob, TeamUserJobPK> {

    @Query("SELECT tuj FROM TeamUserJob tuj WHERE tuj.teamUser.teamId = :teamId AND tuj.status = :status")
    List<TeamUserJob> findByTeamUserTeamIdAndStatus(Long teamId, TeamUserStatus status);

    @Query("SELECT tuj FROM TeamUserJob tuj WHERE tuj.teamJob.team = :teamId AND tuj.status = :status")
    List<TeamUserJob> findByTeamJobTeamIdAndStatus(Team team, TeamUserStatus status);
}
