package peer.backend.dto.admin.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.NoticeTargetType;

@Getter
public class SendNoticeRequest {

    @NotBlank(message = "알림 제목은 필수입니다!")
    private String title;

    @NotBlank(message = "알림 내용은 필수입니다!")
    private String content;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservationDate;

    private List<Long> idList;

    @ValidEnum(enumClass = NoticeTargetType.class)
    private NoticeTargetType noticeTargetType;
}
