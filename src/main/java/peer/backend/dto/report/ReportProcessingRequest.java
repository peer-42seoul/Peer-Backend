package peer.backend.dto.report;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.report.ReportProcessingStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportProcessingRequest {

    @NotNull
    private List<Long> idList;

    @ValidEnum(enumClass = ReportProcessingStatus.class, message = "잘못된 신고 처리 유형입니다.")
    private ReportProcessingStatus type;

    private String content;
}
