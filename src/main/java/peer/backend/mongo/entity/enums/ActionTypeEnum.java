package peer.backend.mongo.entity.enums;


import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActionTypeEnum {

    REGISTRATION("가입", 1L),
    WRITING("글쓰기", 2L),
    COMMENTING("댓글쓰기", 3L),
    WITHDRAWAL("탈퇴", 4L),
    TEAM_FORMATION("팀 결성", 5L),
    EVENT("이벤트", 6L);

    private final String value;
    private final Long code;


    public static ActionTypeEnum ofCode(Long dbData) {
        return Arrays.stream(ActionTypeEnum.values())
            .filter(v -> v.getCode().equals(dbData))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 액션 유형입니다."));
    }
}
