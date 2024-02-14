package peer.backend.repository.team;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.team.enums.TeamUserStatus;

import java.util.List;

public interface TeamUserRepository extends JpaRepository<TeamUser, Long> {

    Optional<TeamUser> findByUserIdAndTeamId(Long userId, Long teamId);

    List<TeamUser> findByUserId(Long userId);

    List<TeamUser> findByTeamId(Long teamId);

    @Query("select count(t) > 0 from TeamUser t where t.teamId = :teamId and t.userId = :userId and t.status = 'APPROVED'")
    Boolean existsAndMemberByUserIdAndTeamId(Long userId, Long teamId);
    Boolean existsByUserIdAndTeamId(Long userId, Long teamId);

    @Query("select count(t) > 0 from TeamUser t where t.teamId = :teamId and t.userId = :userId and t.role = 'LEADER'")
    Boolean existsAndLeaderByUserIdAndTeamId(Long userId, Long teamId);

    List<TeamUser> findByTeamIdAndStatus(Long teamId, TeamUserStatus status);

    @Query("select count(t) > 1 from TeamUser t where t.teamId = :teamId and t.status = 'APPROVED'")
    Boolean existsApprovedByTeamId(Long teamId);

    Boolean existsByUserIdAndTeamIdAndStatus(Long userId, Long teamId, TeamUserStatus status);

    @Query("select t.role from TeamUser t where t.teamId = :teamId and t.userId = :userId")
    TeamUserRoleType findTeamUserRoleTypeByTeamIdAndUserId(Long teamId, Long userId);

    @Query("select t from TeamUser t where t.teamId IN :ids")
    List<TeamUser> findByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT m.userId FROM TeamUser m WHERE m.teamId IN :teamId AND m.user.activated = true")
    List<Long> findAllUserIdsIn(@Param("teamId") List<Long> teamId);

    @Query("SELECT m.userId FROM TeamUser m WHERE m.teamId = :teamId AND m.user.activated = true")
    List<Long> findUserIdsIn(@Param("teamId") Long teamId);
}
