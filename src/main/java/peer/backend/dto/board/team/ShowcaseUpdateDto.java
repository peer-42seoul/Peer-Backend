package peer.backend.dto.board.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ShowcaseUpdateDto {
    private String image;
    @NotNull
    private String content;
    private List<PostLinkResponse> links;
}
