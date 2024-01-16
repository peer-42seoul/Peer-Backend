package peer.backend.dto.board.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.board.team.PostLink;

@Getter
@RequiredArgsConstructor
public class PostLinkResponse {
    private String name;
    private String link;

    public PostLinkResponse(PostLink postLink){
        this.name = postLink.getName();
        this.link = postLink.getUrl();
    }
}
