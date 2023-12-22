package peer.backend.dto.banner;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import peer.backend.dto.notice.CreateNoticeRequest;

@Getter
public class UpdateBannerRequest extends CreateBannerRequest {

    @NotNull(message = "배너 ID는 필수입니다.")
    private Long bannerId;
}
