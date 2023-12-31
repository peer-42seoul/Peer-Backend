package peer.backend.entity.report;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportHandleType {
    FINISHED("종결", 1L),
    PERMANENT_BAN("영구정지", 2L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static ReportHandleType from(String value) {
        for (ReportHandleType status : ReportHandleType.values()) {
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

    public static ReportHandleType ofCode(Long dbData) {
        return Arrays.stream(ReportHandleType.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고 상태 코드입니다."));
    }
}
