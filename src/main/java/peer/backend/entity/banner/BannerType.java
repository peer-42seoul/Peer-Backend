package peer.backend.entity.banner;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BannerType {
    BIG_BANNER("큰 배너", 1L),
    SMALL_BANNER("작은 배너", 2L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static BannerType from(String value) {
        for (BannerType type : BannerType.values()) {
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

    public static BannerType ofCode(Long dbData) {
        return Arrays.stream(BannerType.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너 유형입니다."));
    }
}
