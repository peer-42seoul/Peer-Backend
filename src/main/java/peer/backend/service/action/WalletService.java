package peer.backend.service.action;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import peer.backend.entity.action.ActionType;
import peer.backend.entity.action.Wallet;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.action.WalletRepository;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final ActionTypeService actionTypeService;

    @Transactional
    public Wallet getWalletToActionTypeCode(Long code) {
        return this.walletRepository.findById(code)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 액션 유형 코드입니다."));
    }

    @Transactional
    public Page<Wallet> getWalletList(Pageable pageable) {
        return this.walletRepository.findAll(pageable);
    }

    @Transactional
    public void saveWallet(String actionTypeName, Long value) {
        ActionType actionType = this.actionTypeService.saveActionType(actionTypeName);
        this.walletRepository.save(new Wallet(actionType, value));
    }

    @Transactional
    public void updateWallet(Long code, String actionTypeName, Long value) {
        Wallet wallet = this.getWalletByCode(code);
        ActionType actionType = wallet.getActionType();
        actionType.setActionTypeName(actionTypeName);
        wallet.setValue(value);
    }

    @Transactional
    public Wallet getWalletByCode(Long code) {
        return this.walletRepository.findByActionTypeCode(code)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 코드입니다."));
    }
}
