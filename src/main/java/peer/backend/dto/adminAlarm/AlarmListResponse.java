package peer.backend.dto.adminAlarm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AlarmListResponse {

    private Long notificationId;
    private String title;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime reservationDate;
    private String numberOfTarget;

    // TODO: create a constructor that converts entity to dto
}
