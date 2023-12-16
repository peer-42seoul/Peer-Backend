package peer.backend.dto.wallet;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateWalletItemRequest extends CreateWalletItemRequest {

    @NotNull(message = "액션 유형 코드는 필수입니다.")
    private Long actionTypeCode;
}
