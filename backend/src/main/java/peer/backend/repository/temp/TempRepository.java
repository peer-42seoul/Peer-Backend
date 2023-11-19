package peer.backend.repository.temp;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.temp.Temp;

public interface TempRepository extends JpaRepository<Temp, Long> {
}
