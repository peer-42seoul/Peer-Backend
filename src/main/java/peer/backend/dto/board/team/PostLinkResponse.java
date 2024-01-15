package peer.backend.dto.board.team;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class PostLinkResponse {
    private String name;
    private String link;
}
