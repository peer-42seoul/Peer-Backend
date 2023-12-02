package peer.backend.entity.board.team.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {
    NORMAL("NORMAL"),
    ADMIN("ADMIN")
    ;
    private final String type;
}
