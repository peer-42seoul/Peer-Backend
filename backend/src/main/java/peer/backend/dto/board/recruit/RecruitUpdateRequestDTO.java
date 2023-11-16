package peer.backend.dto.board.recruit;

import lombok.*;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;
import peer.backend.entity.board.recruit.enums.RecruitStatus;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitUpdateRequestDTO {
    private Long user_id;
    private String name;
    private String title;
    private RecruitStatus status;
    private String due;
    private String content;
    private List<String> region;
    private String link;
    private List<String> tagList;
    private String place;
    private List<RecruitRoleDTO> roleList;
    private List<RecruitInterviewDto> interviewList;
}
