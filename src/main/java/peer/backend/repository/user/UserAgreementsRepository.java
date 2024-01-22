package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.UserAgreements;

public interface UserAgreementsRepository extends JpaRepository<UserAgreements, Long> {
}
