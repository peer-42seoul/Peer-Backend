package peer.backend.repository.board.team;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.board.team.enums.BoardType;
import peer.backend.entity.team.Team;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByName(String name);

    Optional<Board> findByTeamAndName(Team team, String name);


    List<Board> findBoardsByTeamIdAndType(Long teamId, BoardType type);

    Optional<Board> findByTeamIdAndType(Long teamId, BoardType type);
}
