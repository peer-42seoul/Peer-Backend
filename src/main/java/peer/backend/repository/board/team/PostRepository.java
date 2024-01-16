package peer.backend.repository.board.team;

import java.util.List;
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


    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId AND (p.title LIKE CONCAT('%', :keyword, '%') OR p.content LIKE CONCAT('%', :keyword, '%')) ORDER BY p.id DESC")
    Page<Post> findByBoardIdAndTitleOrContentContaining(Long boardId, String keyword, Pageable pageable);
}