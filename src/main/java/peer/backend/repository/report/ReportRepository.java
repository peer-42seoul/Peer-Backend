package peer.backend.repository.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import peer.backend.entity.report.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Page<Report> findAll(Pageable pageable);
}
