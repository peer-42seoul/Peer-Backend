package peer.backend.dto.board.recruit;

import lombok.*;
import peer.backend.entity.board.recruit.enums.RecruitDueEnum;

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
    private List<String> due;
    private RecruitDueEnum start = RecruitDueEnum.from(due.get(0));
    private RecruitDueEnum end = RecruitDueEnum.from(due.get(1));
    private String region1;
    private String region2;
    private List<String> place;
    private List<String> status;
    private List<String> tag;
}