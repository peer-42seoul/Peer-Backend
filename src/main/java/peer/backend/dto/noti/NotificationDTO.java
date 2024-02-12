package peer.backend.dto.noti;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.entity.noti.Notification;

import java.time.LocalDateTime;

@Builder
@RequiredArgsConstructor
public class NotificationDTO {
    private String title;
    private String body;
    private String redirectUrl;
    private LocalDateTime issuedAt;
    private Long notificationId;
    private String type;
    private String iconUrl;

    @JsonProperty("isEnd")
    private boolean end;

    NotificationDTO(Notification data, boolean end) {
        this.title = data.getTitle();
        this.body = data.getBody();
        this.redirectUrl = data.getLinkData();
        this.issuedAt = data.getCreatedAt();
        this.notificationId = data.getId();
        this.type = data.getMessageType().getValue();
        this.iconUrl = data.getImageUrl();
        this.end = end;
    }
}
