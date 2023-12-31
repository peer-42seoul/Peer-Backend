package peer.backend.entity.team.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamType {
    STUDY("STUDY"),
    PROJECT("PROJECT");

    private final String value;

    @JsonCreator
    public static TeamType from(String value) {
        for (TeamType status : TeamType.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
