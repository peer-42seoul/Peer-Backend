package peer.backend.dto.team;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.board.recruit.RecruitAnswerDto;
import peer.backend.entity.composite.TeamUserJobPK;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TeamApplicantListDto {

    private String name;
    private Long userId;
    private TeamUserJobPK applyId;
    private String jobName;
    private List<RecruitAnswerDto> answers;
    private String image;
}
