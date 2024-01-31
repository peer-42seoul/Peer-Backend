package peer.backend.repository.board.team;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.board.team.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPostId(Long postId);
}
