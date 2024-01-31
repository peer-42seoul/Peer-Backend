package peer.backend.dto.board.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import peer.backend.entity.board.team.PostComment;
import peer.backend.entity.user.User;

@Getter
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(value = {"author"})
public class PostCommentListResponse {
    private Long commentId;
    private String authorImage;
    private String authorNickname;
    private String content;
    private String createAt;
    @JsonProperty("isAuthor")
    private boolean isAuthor;

    public PostCommentListResponse(PostComment comment, User user){
        User author = comment.getUser();
        this.commentId = comment.getId();
        this.authorImage = (author == null) ? null : author.getImageUrl();
        this.authorNickname = (author == null) ? "탈퇴한 유저" : author.getNickname();
        this.content = comment.getContent();
        this.createAt = comment.getCreatedAt().toString();
        this.isAuthor = user != null && user.equals(author);
    }
}
