package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitType {
    STUDY("STUDY"),
    PROJECT("PROJECT")
    ;

    private final String type;
}
