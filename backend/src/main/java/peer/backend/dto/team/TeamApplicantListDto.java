package peer.backend.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.board.recruit.RecruitAnswerDto;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TeamApplicantListDto {
    String nickName;
    Long recruitId;
    ArrayList<RecruitAnswerDto> answers;
}
