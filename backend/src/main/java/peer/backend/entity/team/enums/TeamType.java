package peer.backend.entity.team.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamType {
    STUDY("스터디"),
    PROJECT("프로젝트");

    private final String value;
}
