package peer.backend.dto.board.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.board.team.enums.BoardType;

@Getter
@RequiredArgsConstructor
public class BoardUpdateRequest {
    private String name;
}
