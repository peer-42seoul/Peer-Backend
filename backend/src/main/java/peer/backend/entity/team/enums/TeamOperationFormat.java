package peer.backend.entity.team.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamOperationFormat {
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    MIX("혼합");

    private final String format;
}
