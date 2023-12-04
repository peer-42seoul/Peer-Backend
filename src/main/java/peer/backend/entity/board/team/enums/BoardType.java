package peer.backend.entity.board.team.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.board.recruit.enums.RecruitStatus;

@Getter
@RequiredArgsConstructor
public enum BoardType {
    NORMAL("NORMAL"),
    ADMIN("ADMIN"),
    SHOWCASE("SHOWCASE")
    ;
    private final String type;

    @JsonCreator
    public static BoardType from(String value) {
        for (BoardType type : BoardType.values()) {
            if (type.getType().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
