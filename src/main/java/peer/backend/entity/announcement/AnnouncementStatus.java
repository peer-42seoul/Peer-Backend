package peer.backend.entity.announcement;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnnouncementStatus {
    PUBLISHED("게재", 1L),
    RESERVATION("예약", 2L),
    HIDING("숨김", 3L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static AnnouncementStatus from(String value) {
        for (AnnouncementStatus status : AnnouncementStatus.values()) {
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

    public static AnnouncementStatus ofCode(Long dbData) {
        return Arrays.stream(AnnouncementStatus.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림 상태입니다."));
    }
}
