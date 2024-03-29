package peer.backend.dto.noti.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Priority {
    IMMEDIATE("IMMEDIATE"),
    SCHEDULED("SCHEDULED"),
    FORCED("FORCED");
    private final String value;
}
