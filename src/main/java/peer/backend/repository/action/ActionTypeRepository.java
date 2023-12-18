package peer.backend.repository.action;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.action.ActionType;

public interface ActionTypeRepository extends JpaRepository<ActionType, Long> {

}
