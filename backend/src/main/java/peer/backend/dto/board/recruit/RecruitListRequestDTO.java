package peer.backend.dto.board.recruit;

import lombok.*;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;

import java.util.List;


//   "title" : "string"
//           "due" : "string"
//           "type" : enum
//    "content" : "string"
//            "user_id" : " number",
//            "region" : "string"
//            "link" : "string"
//            "tag" ": "tagList[]"
//            "role" "List<Role>" { "role name" : "프론트", "number" : 5 }
//            "interviewList" : interviewList
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitListRequestDTO {
    private String name;
    private String title;
    private String due;
    private String place;
    private String type;
    private String content;
    private List<String> region;
    private String link;
    private String status;
    private String thumbnailUrl;
    private List<String> tagList;
    private List<RecruitRole> roleList;
    private List<RecruitInterview> interviewList;
}
