package peer.backend.dto.board.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ShowcaseCreateDto {
    @NotNull
    private String image;
    @NotNull
    private Long teamId;
    @NotNull
    private String content;
    private List<PostLinkResponse> links;
}
