package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitApplicantStatus {
    APPROVED("승인"),
    DENIED("보류"),
    PENDING("보류")
    ;
    private final String status;
}
