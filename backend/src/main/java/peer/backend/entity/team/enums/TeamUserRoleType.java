package peer.backend.entity.team.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamUserRoleType {
    LEADER("리더"),
    MEMBER("멤버");

    private final String value;

    @JsonCreator
    public static TeamUserRoleType from(String value) {
        for (TeamUserRoleType status : TeamUserRoleType.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @JsonCreator
    public String getValue() {
        return value;
    }
}