package peer.backend.dto.profile.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FavoriteResponse {
    Long postId;
    String title;
    String image;
    Long userId;
    String userNickname;
    String userImage;
    String status;
    List<String> tagList;
}