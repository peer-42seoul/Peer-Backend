package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitPlace {
    ONLINE("ONLINE"),
    OFFLINE("OFFLINE"),
    MIX("MIX")
    ;
    private final String place;
}
