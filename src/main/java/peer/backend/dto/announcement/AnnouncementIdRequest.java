package peer.backend.dto.announcement;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AnnouncementIdRequest {

    @NotNull(message = "공지의 ID는 필수입니다.")
    private Long announcementId;
}
