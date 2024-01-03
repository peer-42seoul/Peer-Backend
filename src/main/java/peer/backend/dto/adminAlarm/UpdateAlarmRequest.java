package peer.backend.dto.adminAlarm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateAlarmRequest {

    @NotNull(message = "알림 Id는 필수입니다!")
    private Long alarmId;

    @NotBlank(message = "제목은 필수입니다!")
    private String title;

    @NotBlank(message = "내용은 필수입니다!")
    private String content;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservationDate;
}
