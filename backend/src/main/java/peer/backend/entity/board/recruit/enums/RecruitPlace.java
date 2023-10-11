package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitPlace {
    ONLINE("온라인"),
    OFFLINE("오프라인"),
    MIX("혼합")
    ;
    private final String place;
}
