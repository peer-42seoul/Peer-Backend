package peer.backend.repository.report;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import peer.backend.entity.report.Report;
import peer.backend.entity.report.ReportStatus;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Page<Report> findAllByOrderByStatusAscCreatedAtDesc(Pageable pageable);

    List<Report> findAllByIdIn(List<Long> idList);

    @Query("SELECT r FROM Report r LEFT JOIN Blacklist bl ON r.toUser = bl.user WHERE r.id IN :idList AND r.status != :status AND bl.user IS NULL")
    List<Report> findAllByIdInWithoutStatusWithoutBlacklist(List<Long> idList, ReportStatus status);
}
