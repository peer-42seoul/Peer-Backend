package peer.backend.repository.action;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.action.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query(value = "SELECT w FROM Wallet w JOIN FETCH w.actionType",
        countQuery = "SELECT count(w) FROM Wallet w")
    Page<Wallet> findAll(Pageable pageable);

    @Query("SELECT w FROM Wallet w LEFT JOIN FETCH w.actionType WHERE w.actionTypeCode = :code")
    Optional<Wallet> findByActionTypeCode(Long code);
}
