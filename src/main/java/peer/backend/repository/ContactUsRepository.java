package peer.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.ContactUs;

import java.util.List;

public interface ContactUsRepository extends JpaRepository<ContactUs, Long> {

    @Query("SELECT m FROM ContactUs m WHERE m.emailClientSent = FALSE ")
    public List<ContactUs> findAllForClient();

    @Query("SELECT m FROM ContactUs m WHERE m.emailManagementSent = FALSE ")
    public List<ContactUs> findAllForManeger();


}
