package peer.backend.entity.team.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamUserStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED")
    ;
    private final String status;
}
