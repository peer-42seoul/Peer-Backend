package peer.backend.mongo.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.team.enums.TeamMemberStatus;

@Getter
@RequiredArgsConstructor
public enum UserTrackingStatus {
    BAN("영구 밴"),
    BLACKHOLE("블랙홀"),
    OUTER("아우터"),
    TEMPORARY_BAN("임시 밴"),
    NORMAL("통상 상태");

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
