package peer.backend.dto.noti.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduledStandard {
    BEFORE("BEFORE"),
    AFTER("AFTER");

    private final String scheduledStandard;
}
