package peer.backend.repository.board.team;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.board.team.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
