package peer.backend.dto.board.recruit;

import lombok.*;
import peer.backend.dto.team.TeamJobDto;
import peer.backend.exception.IllegalArgumentException;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitUpdateRequestDTO {
    @NotNull
    @Size(min = 2, max = 30, message = "팀 이름은 2글자 이상 30글자 이하로 작성해 주세요.")
    private String name;
    @NotNull(message = "제목이 반드시 필요합니다.")
    @Size(min = 1, max = 100, message = "제목은 1~100글자로 작성해주세요.")
    private String title;
    @NotNull
    private String status;
    @NotNull
    private String type;
    @NotNull
    private String due;
    @NotNull
    private String content;
    private List<String> region;
    private String link;
    private List<Long> tagList;
    @NotNull
    private String place;
    private List<TeamJobDto> roleList;
    private List<RecruitInterviewDto> interviewList;
    private String image;
    private int max;

    public String getRegion1() {
        if ((this.region == null && this.place.equals("OFFLINE")) ||
                (this.region != null && this.region.size() != 2))
            throw new IllegalArgumentException("잘못된 지역입니다.");
        return (this.region == null ? null : region.get(0));
    }

    public String getRegion2() {
        return (this.region == null ? null : region.get(1));
    }
}
