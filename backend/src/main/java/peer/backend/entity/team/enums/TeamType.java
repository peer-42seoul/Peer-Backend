package peer.backend.entity.team.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamType {
    STUDY("study"),
    PROJECT("project");

    private final String type;
}
