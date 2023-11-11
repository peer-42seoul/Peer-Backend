package peer.backend.dto.profile.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import peer.backend.dto.board.recruit.TagListResponse;

import java.util.List;

@Getter
@Setter
@Builder
public class FavoriteResponse {
    Long recruit_id;
    String title;
    String image;
    Long userId;
    String userNickname;
    String userImage;
    String status;
    List<TagListResponse> tagList;
}