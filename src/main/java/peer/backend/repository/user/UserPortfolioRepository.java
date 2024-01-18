package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.UserPortfolio;

public interface UserPortfolioRepository extends JpaRepository<UserPortfolio, Long> {
}
