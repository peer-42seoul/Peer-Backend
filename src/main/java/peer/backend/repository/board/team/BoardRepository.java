package peer.backend.repository.board.team;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.board.team.Board;
import peer.backend.entity.team.Team;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByName(String name);

    Optional<Board> findByTeamAndName(Team team, String name);
}
