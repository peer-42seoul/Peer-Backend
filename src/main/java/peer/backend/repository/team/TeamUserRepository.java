package peer.backend.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.team.enums.TeamUserStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    TeamUser findByUserIdAndTeamId(Long userId, Long teamId);

    List<TeamUser> findByUserId(Long userId);

    List<TeamUser> findByTeamId(Long teamId);

    boolean existsTeamUserByStatus(TeamUserStatus status);

    Optional<TeamUser> findByJobsAndTeamIdAndUserId(String job, Long teamId, Long userId);

//    @Query("SELECT tu FROM TeamUser tu WHERE tu.userId = :userId AND tu.teamId = :teamId AND tu.job IN :jobs")
//    List<TeamUser> findByUserIdAndTeamIdAndJobs(Long userId, Long teamId, List<String> jobs);

    Boolean existsByUserIdAndTeamId(Long userId, Long teamId);

    @Query("select t.role from TeamUser t where t.teamId = :teamId and t.userId = :userId")
    TeamUserRoleType findTeamUserRoleTypeByTeamIdAndUserId(Long teamId, Long userId);
}
