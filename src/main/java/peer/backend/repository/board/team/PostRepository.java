package peer.backend.repository.board.team;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.team.Team;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByBoardTypeOrderByCreatedAtDesc(BoardType type, Pageable pageable);
}
