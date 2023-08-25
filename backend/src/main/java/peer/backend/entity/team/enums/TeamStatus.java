package peer.backend.entity.team.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TeamStatus {
    RECRUITING("모집 중"),
    BEFORE("시작 전"),
    ONGOING("진행 중"),
    COMPLETE("완료");

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
}
