package peer.backend.dto.profile.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import peer.backend.dto.tag.TagResponse;
import peer.backend.entity.board.team.Post;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ShowcaseFavoriteResponse {
    private Long showcaseId;
    private String image;
    private String teamLogo;
    private String teamName;
    private String content;
    private List<TagResponse> tags;

    public ShowcaseFavoriteResponse(Post post, List<TagResponse> tagList){
        this.showcaseId = post.getId();
        this.image = post.getFiles().get(0).getUrl();
        this.teamLogo = post.getBoard().getTeam().getTeamLogoPath();
        this.content = post.getContent();
        this.tags = tagList;
    }
}
