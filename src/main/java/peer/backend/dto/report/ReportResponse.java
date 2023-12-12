package peer.backend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.report.Report;
import peer.backend.entity.report.ReportStatus;
import peer.backend.entity.report.ReportType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long id;
    private String nickname;
    private String name;
    private String email;
    private ReportType type;
    private String content;
    private String reporter;
    private ReportStatus status;

    public ReportResponse(Report report) {
        this.id = report.getId();
        this.nickname = report.getToUser().getNickname();
        this.name = report.getToUser().getName();
        this.email = report.getToUser().getEmail();
        this.type = report.getType();
        this.content = report.getContent();
        this.reporter = report.getFromUser().getNickname();
        this.status = report.getStatus();
    }
}
