package peer.backend.entity.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.team.enums.TeamMemberStatus;

@Getter
@RequiredArgsConstructor
public enum ReportType {
    ADVERTISING("광고"),
    SENSATIONALISM("선정성"),
    CALUMNY("비방");

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
