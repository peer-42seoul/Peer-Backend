package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitStatus {
    BEFORE("모집전"),
    ONGOING("모집중"),
    DONE("모집완료")
    ;
    private final String status;
}
