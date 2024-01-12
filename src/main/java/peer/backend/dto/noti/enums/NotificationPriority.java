package peer.backend.dto.noti.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationPriority {
    IMMEDIATE("IMMEDIATE"),
    SCHEDULED("SCHEDULED"),
    FORCE("FORCED");

    private final String notificationPriority;
}
