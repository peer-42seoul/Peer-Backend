package peer.backend.dto.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RecruitAnswerDto {
    String answer;
    String question;
    String type;
    List<String> option;
}