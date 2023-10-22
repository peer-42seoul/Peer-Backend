package peer.backend.entity.board.recruit.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitType {
    STUDY("스터디"),
    PROJECT("프로젝트")
    ;
    private final String type;
}
