package peer.backend.entity.composite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.entity.board.team.enums.PostLikeType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikePK implements Serializable {
    private Long userId;
    private Long postId;
    private PostLikeType type;
}
