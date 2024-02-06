package peer.backend.entity.team.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TeamStatus {
    RECRUITING("RECRUITING"),
    BEFORE("BEFORE"),
    ONGOING("ONGOING"),
    COMPLETE("COMPLETE"),
    DISPERSE("DISPERSE");

    private final String value;

    @JsonCreator
    public static TeamStatus from(String value) {
        for (TeamStatus status : TeamStatus.values()) {
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

    public int getOrdinal() {
        return this.ordinal();
    }
}
