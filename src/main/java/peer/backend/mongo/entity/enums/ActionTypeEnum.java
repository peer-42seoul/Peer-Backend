package peer.backend.mongo.entity.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActionTypeEnum {

    REGISTRATION("가입", 1L),
    RECRUIT_WRITING("모집 글쓰기", 2L),
    COMMENTING("댓글쓰기", 3L),
    WITHDRAWAL("탈퇴", 4L),
    TEAM_FORMATION("팀 결성", 5L),
    EVENT("이벤트", 6L),
    TEAM_POST_WRITING("팀 게시판 글쓰기", 7L);

    private final String value;
    private final Long code;

    @JsonCreator
    public static ActionTypeEnum from(String value) {
        for (ActionTypeEnum type : ActionTypeEnum.values()) {
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

    public static ActionTypeEnum ofCode(Long dbData) {
        return Arrays.stream(ActionTypeEnum.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 액션 유형입니다."));
    }
}
