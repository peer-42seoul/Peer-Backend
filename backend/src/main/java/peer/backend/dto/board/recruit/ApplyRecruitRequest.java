package peer.backend.dto.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApplyRecruitRequest {
    private Long user_id;
    private String role;
    private List<String> answerList;
}
