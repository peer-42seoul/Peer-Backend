package peer.backend.dto.profile;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import peer.backend.dto.profile.response.FavoriteResponse;
import java.util.List;

@Getter
@Setter
@Builder
public class FavoritePage {
    List<FavoriteResponse> postList;
    boolean isLast;
}
