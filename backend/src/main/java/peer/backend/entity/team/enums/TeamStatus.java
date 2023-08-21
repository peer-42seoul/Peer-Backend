package peer.backend.entity.team.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamStatus {
    RECRUITING("모집 중"),
    BEFORE("시작 전"),
    ONGOING("진행 중"),
    COMPLETE("완료");

    private final String status;
}
