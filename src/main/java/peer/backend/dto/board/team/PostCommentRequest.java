package peer.backend.dto.board.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class PostCommentRequest {
    @NotNull
    @Size(max = 300)
    private String content;
    private Long teamId;
    @NotNull
    private Long postId;
}
