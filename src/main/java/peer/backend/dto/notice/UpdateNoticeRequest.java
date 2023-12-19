package peer.backend.dto.notice;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateNoticeRequest extends CreateNoticeRequest {

    @NotNull(message = "공지사항 ID는 필수입니다.")
    private Long noticeId;
}
