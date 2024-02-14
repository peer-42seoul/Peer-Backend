package peer.backend.dto.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamApplyDataDTO{
    private Long teamJobId;
    private String name;
    private Integer max;
    private Long teamId;
    private Long pendingNumber;
    private Long applyNumber;

//    ava.lang.Long, java.lang.String, java.lang.Integer, java.lang.Long, int, int
//    long, java.lang.String, int, long, long, long
    public TeamApplyDataDTO(long teamJobId, String name, int max, long teamId, long pendingNumber, long applyNumber) {
        this.teamJobId = teamJobId;
        this.name = name;
        this.max = max;
        this.teamId = teamId;
        this.pendingNumber = pendingNumber;
        this.applyNumber = applyNumber;
    }
}
