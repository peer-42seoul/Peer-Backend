package peer.backend.entity.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {
    ADVERTISING("광고", 1L),
    CALUMNY("비방", 2L),
    SENSATIONALISM("선정성", 3L),
    SPAM("도배", 4L),
    ETC("기타", 5L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static ReportType from(String value) {
        for (ReportType type : ReportType.values()) {
            if (type.getValue().equals(value)) {
                return type;
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
