package peer.backend.dto.banner;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BannerIdRequest {

    @NotNull(message = "배너의 ID는 필수입니다.")
    private Long bannerId;
}
