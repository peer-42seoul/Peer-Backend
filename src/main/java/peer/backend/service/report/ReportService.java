package peer.backend.service.report;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import peer.backend.entity.report.Report;
import peer.backend.entity.report.ReportType;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.report.ReportRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.UserService;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public void save(Long fromId, Long toId, ReportType type, String content) {
        User from = userRepository.getReferenceById(fromId);
        User to;

        try {
            to = userRepository.getReferenceById(toId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("존재하지 않는 유저입니다!");
        }

        this.reportRepository.save(new Report(from, to, type, content));
    }

    @Transactional
    public Page<Report> getReportList(Pageable pageable) {
        return this.reportRepository.findAllByOrderByStatusAsc(pageable);
    }
}
