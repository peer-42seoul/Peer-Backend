package peer.backend.dto.board.recruit;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RecruitFavoriteResponse {
    private Long recruit_id;
    private boolean favorite;
}
