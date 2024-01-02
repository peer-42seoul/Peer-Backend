package peer.backend.repository.notice;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.notice.Notice;
import peer.backend.entity.notice.NoticeStatus;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    public List<Notice> findAllByNoticeStatus(NoticeStatus status);
}
