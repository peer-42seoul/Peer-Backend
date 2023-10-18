package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitApplicantStatus {
    approved("승인"),
    denied("보류"),
    pending("보류")
    ;
    private final String status;
}

//denied("거절")