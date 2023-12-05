package peer.backend.entity.board.team.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostLikeType {
    FAVORITE("FAVORITE"),
    LIKE("LIKE")
    ;
    private final String type;
}
