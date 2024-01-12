package peer.backend.dto.noti.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    ALL("ALL"),
    MESSAGE("MESSAGE"),
    TEAM("TEAM"),
    SYSTEM("SYSTEM");

    private final String value;
}
