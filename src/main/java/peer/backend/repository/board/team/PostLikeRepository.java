package peer.backend.repository.board.team;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.board.team.PostLike;
import peer.backend.entity.composite.PostLikePK;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikePK>{
}
