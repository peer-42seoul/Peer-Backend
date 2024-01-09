package peer.backend.dto.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class HitchResponse {
    private String content;
    private List<String> memberImage;
    private int recruitmentQuota;
}
