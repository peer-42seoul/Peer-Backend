package peer.backend.dto.announcement;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateAnnouncementRequest extends CreateAnnouncementRequest {

    @NotNull(message = "공지사항 ID는 필수입니다.")
    private Long announcementId;
}
