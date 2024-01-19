package peer.backend.dto.board.team;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ShowcaseUpdateDto {
    private String image;
    private String content;
    private List<PostLinkResponse> links;
}
