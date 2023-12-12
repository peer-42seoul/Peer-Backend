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
    private Long userId;

    @ValidEnum(enumClass = ReportType.class, message = "잘못된 신고 유형입니다.")
    private ReportType type;

    @NotBlank(message = "신고 내용은 필수입니다.")
    private String content;
}
