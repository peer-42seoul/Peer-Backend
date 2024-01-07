package peer.backend.service.action;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.entity.action.ActionType;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.action.ActionTypeRepository;

@Service
@RequiredArgsConstructor
public class ActionTypeService {

    private final ActionTypeRepository actionTypeRepository;

    @Transactional
    public ActionType getActionType(Long code) {
        return this.actionTypeRepository.findById(code)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 코드입니다."));
    }

    @Transactional
    public ActionType saveActionType(String actionTypeName) {
        Long code = this.getMaxCode() + 1;
        return this.actionTypeRepository.save(new ActionType(code, actionTypeName));
    }

    @Transactional
    public Long getMaxCode() {
        return this.actionTypeRepository.getMaxCode();
    }
}
