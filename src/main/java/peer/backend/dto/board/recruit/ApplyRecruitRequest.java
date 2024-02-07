package peer.backend.dto.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApplyRecruitRequest {
    @NotNull
    private String role;
    private List<String> answerList;
}
