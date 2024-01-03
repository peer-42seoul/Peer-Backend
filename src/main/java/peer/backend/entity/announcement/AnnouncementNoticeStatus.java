package peer.backend.entity.announcement;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnnouncementNoticeStatus {
    NONE("없음", 1L),
    IMMEDIATELY("즉시", 2L),
    RESERVATION("예약", 3L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static AnnouncementNoticeStatus from(String value) {
        for (AnnouncementNoticeStatus type : AnnouncementNoticeStatus.values()) {
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

    public static AnnouncementNoticeStatus ofCode(Long dbData) {
        return Arrays.stream(AnnouncementNoticeStatus.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림 유형입니다."));
    }
}
