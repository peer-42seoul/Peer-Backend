package peer.backend.dto.admin.notice;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class NoticeIdRequest {

    @NotNull(message = "알림 Id는 필수입니다!")
    private Long noticeId;
}
