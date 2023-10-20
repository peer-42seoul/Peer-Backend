package peer.backend.dto.Board.Recruit;

import lombok.*;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;
import peer.backend.entity.board.recruit.enums.RecruitStatus;

import java.util.List;

@Getter
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
    private String region;
    private String link;
    private List<String> tagList;
    private List<RecruitRole> roleList;
    private List<RecruitInterview> interviewList;
}
