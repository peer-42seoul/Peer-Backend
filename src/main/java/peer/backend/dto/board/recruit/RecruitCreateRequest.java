package peer.backend.dto.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.team.TeamJobDto;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.exception.IllegalArgumentException;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


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

    public List<TeamJobDto> getRoleList() {
        if ((Objects.isNull(this.roleList) || this.roleList.isEmpty())) {
            if (this.type.equals(TeamType.PROJECT.getValue()))
                throw new IllegalArgumentException("프로젝트에는 반드시 역할이 한개 이상 필요합니다.");
        } else {
            if (this.type.equals(TeamType.STUDY.getValue()))
                throw new IllegalArgumentException("스터디에는 역할을 추가할 수 없습니다.");
        }
        return this.roleList;
    }

    public int getMax() {
        if (this.type.equals("STUDY"))
            return this.max;
        return 0;
    }
}
