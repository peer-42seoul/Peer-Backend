package peer.backend.dto.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.board.recruit.enums.RecruitInterviewType;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecruitInterviewDto {
    private String question;
    private RecruitInterviewType type;
    private List<String> optionList;
}
