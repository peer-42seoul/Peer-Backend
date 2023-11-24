package peer.backend.dto.board.recruit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import peer.backend.entity.board.recruit.enums.RecruitDueEnum;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitListRequest{
    @NotNull
    private int page;
    @NotNull
    private int pageSize;
    private String type;
    @NotNull
    private String sort;
    @Pattern(regexp = "^[a-zA-Z0-9\\s~.가-힣]*$", message = "키워드 검색은 한글, 영어, 숫자 및 기호(~.)만 가능합니다.")
    private String keyword;
    private List<String> due;
    private int start;
    private int end;
    @Pattern(regexp = "^[0-9\\s가-힣]*$", message = "지역 검색은 한글과 숫자만 가능합니다.")
    private String region1;
    @Pattern(regexp = "^[0-9\\s가-힣]*$", message = "지역 검색은 한글과 숫자만 가능합니다.")
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