package peer.backend.dto.board.recruit;

import lombok.*;

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
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitListRequest{
    private int page;
    private int pageSize;
    private String type;
    private String sort;
    private String keyword;
    private String due;
    private String region1;
    private String region2;
    private String place;
    private String status;
    private List<String> tag;
}