package peer.backend.dto.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.announcement.AnnouncementNoticeStatus;

@Getter
public class CreateAnnouncementRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Max(30)
    private String title;

    @NotBlank(message = "작성자는 필수입니다.")
    @Max(10)
    private String writer;

    @NotBlank(message = "내용은 필수입니다.")
    @Max(10000)
    private String content;

    @NotBlank(message = "이미지는 필수입니다.")
    private String image;

    @ValidEnum(enumClass = AnnouncementNoticeStatus.class)
    private AnnouncementNoticeStatus announcementNoticeStatus;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservationDate;
}
