package peer.backend.repository.action;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.action.ActionType;

public interface ActionTypeRepository extends JpaRepository<ActionType, Long> {

    @Query("SELECT max(a.code) FROM ActionType a")
    Long getMaxCode();
}
