package peer.backend.repository.blacklist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.blacklist.Blacklist;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {

    Page<Blacklist> findAll(Pageable pageable);
}
