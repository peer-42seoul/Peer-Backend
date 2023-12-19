package peer.backend.repository.team;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.team.Team;
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

    Boolean existsByUserIdAndTeamId(Long userId, Long teamId);

    @Query("select t.role from TeamUser t where t.teamId = :teamId and t.userId = :userId")
    TeamUserRoleType findTeamUserRoleTypeByTeamIdAndUserId(Long teamId, Long userId);
}
