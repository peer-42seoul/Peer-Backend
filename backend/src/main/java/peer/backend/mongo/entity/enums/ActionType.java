package peer.backend.mongo.entity.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActionType {

    REGISTRATION("가입"),
    WRITING("글쓰기"),
    COMMENTING("댓글쓰기"),
    WITHDRAWAL("탈퇴"),
    TEAM_FORMATION("팀 결성"),
    EVENT("이벤트");

    private final String value;
}
