package peer.backend.repository.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.notice.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
