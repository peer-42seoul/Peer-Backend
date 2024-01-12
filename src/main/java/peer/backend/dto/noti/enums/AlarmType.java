package peer.backend.dto.noti.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {
    GENERAL("GENERAL"),
    MESSAGE("MESSAGE"),
    STUDY("STUDY"),
    NOTICE("NOTICE");
    private final String alarmType;
}
