package peer.backend.dto.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.announcement.AnnouncementNoticeStatus;

@Getter
public class UpdateAnnouncementRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 30, message = "길이는 30 이하여야합니다.")
    private String title;

    @NotBlank(message = "작성자는 필수입니다.")
    @Size(max = 10, message = "길이는 10 이하여야합니다.")
    private String writer;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 10000, message = "길이는 10000 이하여야합니다.")
    private String content;

    private String image;

    @ValidEnum(enumClass = AnnouncementNoticeStatus.class)
    private AnnouncementNoticeStatus announcementNoticeStatus;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservationDate;

    @NotNull(message = "공지사항 ID는 필수입니다.")
    private Long announcementId;
}
