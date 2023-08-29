package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.PeerOperation;

public interface PeerOperationRepository extends JpaRepository<PeerOperation, Long> {

}
