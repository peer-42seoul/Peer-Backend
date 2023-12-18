package peer.backend.dto.board.recruit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.team.TeamJobDto;
import peer.backend.exception.IllegalArgumentException;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitCreateRequest {

    private String image;
    @NotNull
    @Size(min = 2, max = 30, message = "팀이름은 2글자 이상 30글자 이하로 작성해주세요.")
    private String name;
    @NotNull(message = "제목이 반드시 필요합니다.")
    @Size(min = 1, max = 100, message = "제목은 1~100글자로 작성해주세요.")
    private String title;
    @NotNull
    private String due;
    @NotNull
    private String place;
    @NotNull
    private String type;
    @Size(min = 1, max = 1000, message = "")
    @NotNull
    @Lob
    private String content;
    private List<String> region;
    private String link;
    private List<Long> tagList;
    private List<TeamJobDto> roleList;
    private List<RecruitInterviewDto> interviewList;
    private List<String> leaderJob;

    public String getRegion1() {
        if ((this.region == null && this.place.equals("OFFLINE")) ||
                (this.region != null && this.region.size() != 2))
            throw new IllegalArgumentException("잘못된 지역입니다.");
        return (this.region == null ? null : region.get(0));
    }

    public String getRegion2() {
        return (this.region == null ? null : region.get(1));
    }

    public List<String> getLeaderJob() {
        if (this.roleList == null && this.leaderJob == null)
            return Collections.emptyList();
        else if (this.roleList != null && !this.roleList.isEmpty() && this.leaderJob != null)
            return this.leaderJob;
        else
            throw new IllegalArgumentException("작성자에게 잘못된 역할을 할당하였습니다.");
    }
}
