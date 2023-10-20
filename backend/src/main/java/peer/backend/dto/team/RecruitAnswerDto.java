package peer.backend.dto.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecruitAnswerDto {
    String answer;
    String question;
    String type;
}
