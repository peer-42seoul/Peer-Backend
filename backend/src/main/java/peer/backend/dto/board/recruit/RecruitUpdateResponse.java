package peer.backend.dto.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.enums.RecruitStatus;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecruitUpdateResponse {
    private String title;
    private int totalNumber;
    private RecruitStatus status;
    private String due;
    private String content;
    private Long leader_id;
    private String region1;
    private String region2;
    private String link;
    private String leader_nickname;
    private String leader_image;
    private List<String> tagList;
    private List<RecruitRoleDTO> roleList;
    private List<RecruitInterviewDto> interviewList;
}