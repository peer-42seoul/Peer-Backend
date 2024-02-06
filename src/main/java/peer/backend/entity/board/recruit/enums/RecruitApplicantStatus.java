package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitApplicantStatus {
    APPROVED("APPROVED"),
    DENIED("DENIED"),
    PENDING("PENDING")
    ;
    private final String status;
}

//denied("거절")