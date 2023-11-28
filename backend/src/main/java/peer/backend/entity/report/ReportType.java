package peer.backend.entity.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.team.enums.TeamMemberStatus;

@Getter
@RequiredArgsConstructor
public enum ReportType {
    ADVERTISING("광고", 1L),
    SENSATIONALISM("선정성", 2L),
    CALUMNY("비방", 3L);

    private final String value;
    private final Long code;

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

    public static ReportType ofCode(Long dbData) {
        return Arrays.stream(ReportType.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 신고 타입 코드입니다."));
    }
}
