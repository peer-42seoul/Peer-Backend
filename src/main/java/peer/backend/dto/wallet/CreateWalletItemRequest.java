package peer.backend.dto.wallet;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateWalletItemRequest {

    @NotBlank(message = "행동 타입명은 필수입니다!")
    private String actionTypeName;

    @NotNull(message = "월렛 값은 필수입니다!")
    private Long wallet;
}
