package peer.backend.repository.user;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.user.UserPortfolio;

import java.util.List;

public interface UserPortfolioRepository extends JpaRepository<UserPortfolio, Long> {

    @Query("SELECT m FROM UserPortfolio m WHERE m.userId = :userId")
    List<UserPortfolio> findByUserId(@Param("userId") long userId);

    @Query("SELECT m FROM UserPortfolio m WHERE m.teamId = :teamId")
    List<UserPortfolio> findByTeamId(@Param("teamId") long teamId);

}
