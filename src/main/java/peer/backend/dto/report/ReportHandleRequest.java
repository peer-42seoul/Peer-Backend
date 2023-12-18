package peer.backend.dto.report;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.report.ReportHandleType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportHandleRequest {

    @NotNull
    private List<Long> idList;

    @ValidEnum(enumClass = ReportHandleType.class, message = "잘못된 신고 처리 유형입니다.")
    private ReportHandleType type;

    @NotNull
    private String content;
}
