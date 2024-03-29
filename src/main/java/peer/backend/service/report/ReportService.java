package peer.backend.service.report;

import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import peer.backend.annotation.tracking.UserReportTracking;
import peer.backend.entity.report.Report;
import peer.backend.entity.report.ReportStatus;
import peer.backend.entity.report.ReportType;
import peer.backend.entity.user.User;
import peer.backend.exception.ConflictException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.report.ReportRepository;
import peer.backend.repository.user.UserRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @UserReportTracking
    @Transactional
    public Report save(User from, Long toId, ReportType type, String content) {
        User to = userRepository.findById(toId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        if (from.getId().equals(to.getId())) {
            throw new ConflictException("자기 자신은 신고할 수 없습니다.");
        }

        return this.reportRepository.save(new Report(from, to, type, content));
    }

    @Transactional
    public Page<Report> getReportList(Pageable pageable) {
        return this.reportRepository.findAllByOrderByStatusAscIdDesc(pageable);
    }

    @Transactional
    public void setReportStatus(List<Long> idList, ReportStatus status) {
        List<Report> reportList = this.getReportListToIdList(idList);

        for (Report report : reportList) {
            report.setStatus(status);
        }
    }

    @Transactional
    public List<Report> getReportListToIdList(List<Long> idList) {
        return this.reportRepository.findAllByIdIn(idList);
    }

    @Transactional
    public List<Report> getReportListToIdListWithoutStatus(List<Long> idList, ReportStatus status) {
        return this.reportRepository.findAllByIdInWithoutStatusWithoutBlacklist(idList,
            status);
    }

}
