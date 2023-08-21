package peer.backend.entity.team.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamMemberStatus {
    CONFIRMED("확정"),
    RECRUITING("모집 중");

    private final String status;
}
