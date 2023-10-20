package peer.backend.dto.profile;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
public class FavoritePage {
    List<FavoriteResponse> postList;
    boolean isLast;

    public FavoritePage(List<FavoriteResponse> list, Pageable pageable) {
        PageImpl<FavoriteResponse> page = new PageImpl<> (list, pageable, list.size());
        this.postList = list;
        this.isLast = page.isLast();
    }
}
