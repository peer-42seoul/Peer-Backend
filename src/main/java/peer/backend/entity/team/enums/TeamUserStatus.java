package peer.backend.entity.team.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamUserStatus {
    PENDING("PENDING"),
    JOINED("JOINED")
    ;

    private final String value;
}
