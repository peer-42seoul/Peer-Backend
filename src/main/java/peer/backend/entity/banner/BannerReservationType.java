package peer.backend.entity.banner;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BannerReservationType {
    IMMEDIATELY("즉시", 1L),
    RESERVATION("예약", 2L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static BannerReservationType from(String value) {
        for (BannerReservationType type : BannerReservationType.values()) {
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

    public static BannerReservationType ofCode(Long dbData) {
        return Arrays.stream(BannerReservationType.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너 상태입니다."));
    }
}
