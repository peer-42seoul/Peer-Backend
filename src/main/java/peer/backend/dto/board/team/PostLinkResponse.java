package peer.backend.dto.board.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.board.team.PostLink;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@RequiredArgsConstructor
public class PostLinkResponse {
    @NotNull
    @Pattern(regexp = "^[\\uAC00-\\uD7A3a-zA-Z0-9]+$")
    private String name;
    @NotNull
    @Pattern(regexp = "(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)")
    private String link;

    public PostLinkResponse(PostLink postLink){
        this.name = postLink.getName();
        this.link = postLink.getUrl();
    }
}
