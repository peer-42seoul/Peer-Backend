package peer.backend.dto.board.recruit;

import com.mongodb.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;
import peer.backend.entity.board.recruit.enums.RecruitPlace;
import peer.backend.entity.board.recruit.enums.RecruitType;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamType;

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
public class RecruitRequestDTO {
    private Long userId;
    private String name;
    private String title;
    private String due;
    private String place;
    private String type;
    private String content;
    private String region;
    private String link;
    private String status;
    private List<String> tagList;
    private List<RecruitRole> roleList;
    private List<RecruitInterview> interviewList;
}
