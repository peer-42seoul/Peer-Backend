package peer.backend.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.report.Report;
import peer.backend.entity.report.ReportType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long from;
    private Long to;
    private ReportType type;
    private String content;

    public ReportResponse(Report report) {
        this.from = report.getFromUser().getId();
        this.to = report.getToUser().getId();
        this.type = report.getType();
        this.content = report.getContent();
    }
}
