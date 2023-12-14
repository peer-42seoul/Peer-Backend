package peer.backend.dto.board.recruit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.tag.TagResponse;
import peer.backend.dto.team.TeamJobDto;
import peer.backend.entity.board.recruit.enums.RecruitStatus;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(value = {"answered"})
public class RecruitUpdateResponse {

    private String title;
    private int totalNumber;
    private RecruitStatus status;
    private String due;
    private String content;
    private Long leader_id;
    private String region1;
    private String region2;
    private String link;
    private String leader_nickname;
    private String leader_image;
    private List<TagResponse> tagList;
    private List<TeamJobDto> roleList;
    private List<RecruitInterviewDto> interviewList;
    @JsonProperty("isAnswered")
    private boolean isAnswered;
    private String place;
    private String type;
}
