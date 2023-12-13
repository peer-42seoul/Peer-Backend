package peer.backend.entity.report;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    WAITING("대기중", 1L),
    COMPLETED("처리완료", 2L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static ReportStatus from(String value) {
        for (ReportStatus status : ReportStatus.values()) {
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

    public static ReportStatus ofCode(Long dbData) {
        return Arrays.stream(ReportStatus.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고 상태 코드입니다."));
    }
}
