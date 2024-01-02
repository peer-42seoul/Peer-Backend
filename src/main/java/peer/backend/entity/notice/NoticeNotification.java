package peer.backend.entity.notice;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeNotification {
    NONE("없음", 1L),
    IMMEDIATELY("즉시", 2L),
    RESERVATION("예약", 3L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static NoticeNotification from(String value) {
        for (NoticeNotification type : NoticeNotification.values()) {
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

    public static NoticeNotification ofCode(Long dbData) {
        return Arrays.stream(NoticeNotification.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림 유형입니다."));
    }
}
