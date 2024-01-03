package peer.backend.entity;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TargetType {
    USER("user", 1L),
    TEAM("team", 2L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static TargetType from(String value) {
        for (TargetType type : TargetType.values()) {
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

    public static TargetType ofCode(Long dbData) {
        return Arrays.stream(TargetType.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림 유형입니다."));
    }
}
