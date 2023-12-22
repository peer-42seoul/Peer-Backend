package peer.backend.entity.banner;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BannerStatus {
    ONGOING("진행 중", 1L),
    RESERVATION("예약", 2L),
    TERMINATION("종료", 3L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static BannerStatus from(String value) {
        for (BannerStatus status : BannerStatus.values()) {
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

    public static BannerStatus ofCode(Long dbData) {
        return Arrays.stream(BannerStatus.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너 상태입니다."));
    }
}
