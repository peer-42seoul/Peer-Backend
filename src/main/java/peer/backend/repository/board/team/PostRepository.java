package peer.backend.repository.board.team;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.enums.BoardType;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByBoardTypeOrderByCreatedAtDesc(BoardType type, Pageable pageable);

    @Query(value = "SELECT * FROM post WHERE board_id = :boardId ORDER BY id DESC",
            nativeQuery = true)
    Page<Post> findPostsByBoardOrderByIdDesc(Long boardId, Pageable pageable);
}
