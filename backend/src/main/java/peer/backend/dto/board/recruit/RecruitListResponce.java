package peer.backend.dto.board.recruit;

import lombok.*;
import peer.backend.entity.board.recruit.enums.RecruitStatus;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruitListResponce {
//    {
//        "postList" :  {
//        "post_id" : 10,
//                "title":"제목",
//                "image":"썸네일url",
//                "user_id":40,
//                "user_nickname":"jwee"
//        "user_thumbnail":"유저썸네일url"
//        "status":"before",
//                "tagList":["java", "spring", "react"]
//        "isFavorite":"true"
//    }
//    }
    private String title;
    private String image;
    private Long user_id;
    private String user_nickname;
    private String user_thumbnail;
    private RecruitStatus status;
    private List<String> tagList;
    private boolean isFavorite;
}
