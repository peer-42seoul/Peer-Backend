package peer.backend.dto.noti.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
    SYSTEM("SYSTEM"),
    MESSAGE("MESSAGE"),
    TEAM("TEAM");

    private final String messageType;
}
