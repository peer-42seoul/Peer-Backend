package peer.backend.repository.board.team;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.team.Team;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByName(String name);

    Optional<Board> findByTeamAndName(Team team, String name);

    @Query(value = "SELECT * FROM board WHERE team_id = :teamId ORDER BY id DESC",
            nativeQuery = true)
    List<Board> findByTeamId(Long teamId);
}
