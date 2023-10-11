package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitInterviewType {
    CLOSE("객관식"),
    OPEN("주관식"),
    MULTIPLE("다중선택"),
    RATIO("선형배율")
    ;
    private final String type;
}
