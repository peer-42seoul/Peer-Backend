package peer.backend.entity.alarm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TargetType {
    ALL("ALL"),
    CERTAIN("CERTAIN");
    private final String targetType;
}
