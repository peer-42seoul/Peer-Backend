package peer.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.user.Applicant;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

}
