package peer.backend.entity.team.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamMemberStatus {
    CONFIRMED("확정"),
    RECRUITING("모집 중");

    private final String value;

    @JsonCreator
    public static TeamMemberStatus from(String value) {
        for (TeamMemberStatus status : TeamMemberStatus.values()) {
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
