package peer.backend.dto.board.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.board.team.enums.BoardType;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCreateRequest {
    private Long teamId;
    private String name;
    private String type;
}
