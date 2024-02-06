package peer.backend.entity.blacklist;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlacklistHandleType {
    FREE("석방", 1L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static BlacklistHandleType from(String value) {
        for (BlacklistHandleType type : BlacklistHandleType.values()) {
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

    public static BlacklistHandleType ofCode(Long dbData) {
        return Arrays.stream(BlacklistHandleType.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 블랙리스트 처리 유형입니다."));
    }
}
