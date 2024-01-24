package peer.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.ContactUs;

public interface ContactUsRepository extends JpaRepository<ContactUs, Long> {
}
