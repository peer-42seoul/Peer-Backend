package peer.backend.repository.team;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.team.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);

    @Query(value = "SELECT t.* FROM team t INNER JOIN team_user tu ON t.id = tu.team_id INNER JOIN user u ON tu.user_id = u.id WHERE t.name LIKE CONCAT('%', :keyword, '%') OR (tu.role = 'LEADER' AND u.nickname LIKE CONCAT('%', :keyword, '%'))", countQuery = "SELECT count(t) FROM Team t", nativeQuery = true)
    Page<Team> findByNameAndLeaderContainingFromPageable(Pageable pageable, String keyword);
}
