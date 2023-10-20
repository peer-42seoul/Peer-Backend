package peer.backend.dto.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TeamApplicantListDto {
    String name;
    Long recruit_id;
    ArrayList<RecruitAnswerDto> answers;
}
