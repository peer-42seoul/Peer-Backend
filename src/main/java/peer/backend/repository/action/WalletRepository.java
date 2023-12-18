package peer.backend.repository.action;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.action.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

}
