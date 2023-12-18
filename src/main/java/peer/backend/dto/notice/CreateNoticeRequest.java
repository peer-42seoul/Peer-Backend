package peer.backend.dto.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.notice.Notification;

@Getter
public class CreateNoticeRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "작성자는 필수입니다.")
    private String writer;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotBlank(message = "이미지는 필수입니다.")
    private String image;

    @ValidEnum(enumClass = Notification.class)
    private Notification notification;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservationDate;
}
