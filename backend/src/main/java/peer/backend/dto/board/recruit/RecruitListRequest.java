package peer.backend.dto.board.recruit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import peer.backend.entity.board.recruit.enums.RecruitDueEnum;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitListRequest{
    private int page;
    private int pageSize;
    private String type;
    private String sort;
    private String keyword;
    private List<String> due;
    private int start;
    private int end;
    private String region1;
    private String region2;
    private List<String> place;
    private List<String> status;
    private List<String> tag;

    @JsonProperty("start")
    public int getStart() {
        return due.get(0) != null ? RecruitDueEnum.from(due.get(0)).getValue() : null;
    }
    @JsonProperty("end")
    public int getEnd() {
        return due.get(1) != null ? RecruitDueEnum.from(due.get(1)).getValue() : null;
    }
}