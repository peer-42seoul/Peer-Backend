package peer.backend.dto.notice;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class NoticeIdRequest {

    @NotNull(message = "공지의 ID는 필수입니다.")
    private Long noticeId;
}
