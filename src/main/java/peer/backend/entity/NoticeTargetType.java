package peer.backend.entity;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeTargetType {
    USER("user", 1L),
    TEAM("team", 2L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static NoticeTargetType from(String value) {
        for (NoticeTargetType type : NoticeTargetType.values()) {
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

    public static NoticeTargetType ofCode(Long dbData) {
        return Arrays.stream(NoticeTargetType.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림 타겟 유형입니다."));
    }
}
