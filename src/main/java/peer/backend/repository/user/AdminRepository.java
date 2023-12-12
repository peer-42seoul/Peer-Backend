package peer.backend.repository.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminId(String id);
}
