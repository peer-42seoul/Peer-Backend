package peer.backend.entity.blacklist;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlacklistType {
    PERMANENT_BAN("영구정지", 1L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static BlacklistType from(String value) {
        for (BlacklistType type : BlacklistType.values()) {
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

    public static BlacklistType ofCode(Long dbData) {
        return Arrays.stream(BlacklistType.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 블랙리스트 유형입니다."));
    }
}
