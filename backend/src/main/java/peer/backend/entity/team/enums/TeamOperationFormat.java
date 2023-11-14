package peer.backend.entity.team.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TeamOperationFormat {
    ONLINE("ONLINE"),
    OFFLINE("OFFLINE"),
    MIX("혼합");

    private final String value;

    @JsonCreator
    public static TeamOperationFormat from(String value) {
        for (TeamOperationFormat status : TeamOperationFormat.values()) {
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
