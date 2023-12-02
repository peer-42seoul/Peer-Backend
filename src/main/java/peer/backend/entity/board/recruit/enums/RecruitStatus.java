package peer.backend.entity.board.recruit.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.team.enums.TeamOperationFormat;

@Getter
@RequiredArgsConstructor
public enum RecruitStatus {
    BEFORE("BEFORE"),
    ONGOING("ONGOING"),
    DONE("DONE")
    ;
    private final String status;

    @JsonCreator
    public static RecruitStatus from(String value) {
        for (RecruitStatus status : RecruitStatus.values()) {
            if (status.getStatus().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
