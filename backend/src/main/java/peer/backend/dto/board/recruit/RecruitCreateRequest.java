package peer.backend.dto.board.recruit;

import lombok.*;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitCreateRequest {
    private String name;
    private String title;
    private String due;
    private String place;
    private String type;
    private String content;
    private List<String> region;
    private String link;
    private String thumbnailUrl;
    private List<String> tagList;
    private List<RecruitRole> roleList;
    private List<RecruitInterview> interviewList;
}
