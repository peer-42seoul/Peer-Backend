package peer.backend.repository.board.team;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.board.team.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}
