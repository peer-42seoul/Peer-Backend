package peer.backend.dto.wallet;

import lombok.Getter;
import peer.backend.entity.action.Wallet;
import peer.backend.mongo.entity.enums.ActionTypeEnum;

@Getter
public class WalletItemResponse {

    private Long actionTypeCode;
    private ActionTypeEnum actionTypeName;
    private Long wallet;

    public WalletItemResponse(Wallet wallet) {
        this.actionTypeCode = wallet.getActionTypeCode();
        this.actionTypeName = ActionTypeEnum.from(wallet.getActionType().getActionTypeName());
        this.wallet = wallet.getValue();
    }
}
