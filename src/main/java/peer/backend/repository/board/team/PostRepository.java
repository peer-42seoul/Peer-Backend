package peer.backend.repository.board.team;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.board.team.enums.PostLikeType;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByBoardTeamIdAndBoardType(Long teamId, BoardType type);
    Page<Post> findAllByBoardTypeAndIsPublicOrderByCreatedAtDesc(BoardType type, boolean isPublic, Pageable pageable);

    @Query(value = "SELECT * FROM post WHERE board_id = :boardId ORDER BY id DESC",
            nativeQuery = true)
    Page<Post> findPostsByBoardOrderByIdDesc(Long boardId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId AND (p.title LIKE CONCAT('%', :keyword, '%') OR p.content LIKE CONCAT('%', :keyword, '%')) ORDER BY p.id DESC")
    Page<Post> findByBoardIdAndTitleOrContentContaining(Long boardId, String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.postLike pl JOIN p.board b WHERE pl.userId = :userId AND pl.type = :type AND p.isPublic = :isPublic AND b.type = :boardType")
    Page<Post> findShowcaseFavoriteList(boolean isPublic, BoardType boardType, Long userId, PostLikeType type, Pageable pageable);
}