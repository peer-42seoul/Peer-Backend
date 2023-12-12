package peer.backend.controller;


import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.report.ReportRequest;
import peer.backend.dto.report.ReportResponse;
import peer.backend.entity.report.Report;
import peer.backend.entity.user.User;
import peer.backend.service.report.ReportService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping()
    public void report(Authentication authentication, @RequestBody @Valid
    ReportRequest request) {
        User user = User.authenticationToUser(authentication);
        this.reportService.save(user.getId(), request.getUserId(), request.getType(),
            request.getContent());
    }

    @GetMapping()
    public List<ReportResponse> getReportList() {
        List<Report> reportList = this.reportService.getReportList();
        return reportList.stream().map(ReportResponse::new)
            .collect(Collectors.toList());
    }
}
