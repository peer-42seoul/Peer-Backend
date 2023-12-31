package peer.backend.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.team.enums.TeamUserStatus;

import java.util.List;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    TeamUser findByUserIdAndTeamId(Long userId, Long teamId);

    List<TeamUser> findByUserId(Long userId);

    List<TeamUser> findByTeamId(Long teamId);

    Boolean existsByUserIdAndTeamId(Long userId, Long teamId);

    List<TeamUser> findByTeamIdAndStatus(Long teamId, TeamUserStatus status);
    Boolean existsByUserIdAndTeamIdAndStatus(Long userId, Long teamId, TeamUserStatus status);

    @Query("select t.role from TeamUser t where t.teamId = :teamId and t.userId = :userId")
    TeamUserRoleType findTeamUserRoleTypeByTeamIdAndUserId(Long teamId, Long userId);
}
