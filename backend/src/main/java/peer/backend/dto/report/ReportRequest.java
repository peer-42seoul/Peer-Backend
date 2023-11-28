package peer.backend.dto.report;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.report.ReportType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    @NotNull
    private Long id;

    @ValidEnum(enumClass = ReportType.class)
    private ReportType type;

    @NotBlank
    private String content;
}
