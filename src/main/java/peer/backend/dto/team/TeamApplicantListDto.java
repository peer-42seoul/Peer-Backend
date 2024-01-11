package peer.backend.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.board.recruit.RecruitAnswerDto;
import peer.backend.entity.composite.TeamUserJobPK;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TeamApplicantListDto {
    String name;
    Long userId;
    TeamUserJobPK applyId;
    String jobName;
    List<RecruitAnswerDto> answers;
}
