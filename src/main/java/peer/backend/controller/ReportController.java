package peer.backend.controller;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.report.ReportHandleRequest;
import peer.backend.dto.report.ReportRequest;
import peer.backend.dto.report.ReportResponse;
import peer.backend.entity.blacklist.BlacklistType;
import peer.backend.entity.report.Report;
import peer.backend.entity.report.ReportHandleType;
import peer.backend.entity.report.ReportStatus;
import peer.backend.entity.user.User;
import peer.backend.service.blacklist.BlacklistService;
import peer.backend.service.report.ReportService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ReportController {

    private final ReportService reportService;
    private final BlacklistService blacklistService;

    @PostMapping("/api/v1/report")
    public void report(Authentication authentication, @RequestBody @Valid
    ReportRequest request) {
        User user = User.authenticationToUser(authentication);
        this.reportService.save(user, request.getUserId(), request.getType(),
            request.getContent());
    }

    @GetMapping("/api/v1/admin/report")
    public Page<ReportResponse> getReportList(Pageable pageable) {
        Page<Report> reportList = this.reportService.getReportList(pageable);
        return reportList.map(ReportResponse::new);
    }

    @PostMapping("/api/v1/admin/report")
    public void handleReport(@RequestBody @Valid ReportHandleRequest request) {
        if (request.getType().equals(ReportHandleType.PERMANENT_BAN)) {
            List<Report> reportList = this.reportService.getReportListToIdListWithoutStatus(
                request.getIdList(), ReportStatus.COMPLETED);
            Set<User> userSet = reportList.stream().map(Report::getToUser)
                .collect(Collectors.toSet());
            BlacklistType type = this.blacklistService.getBlacklistTypeToReportHandleType(
                request.getType());
            this.blacklistService.addBlacklistToUserList(List.copyOf(userSet), type,
                request.getContent());
        }
        this.reportService.setReportStatus(request.getIdList(), ReportStatus.COMPLETED);
    }
}
