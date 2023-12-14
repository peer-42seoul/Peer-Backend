package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitInterviewType {
    CLOSE("CLOSE"),
    OPEN("OPEN"),
    MULTIPLE("CHECK"),
    RATIO("RATIO")
    ;
    private final String type;
}
