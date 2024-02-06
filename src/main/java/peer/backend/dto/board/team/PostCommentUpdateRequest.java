package peer.backend.dto.board.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@RequiredArgsConstructor
public class PostCommentUpdateRequest {
    @NotNull
    @Size(max = 300)
    private String content;
}
