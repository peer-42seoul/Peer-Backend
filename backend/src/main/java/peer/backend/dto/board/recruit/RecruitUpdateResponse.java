package peer.backend.dto.board.recruit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.board.recruit.enums.RecruitStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(value = { "answered" })
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
    private List<TagListResponse> tagList;
    private List<RecruitRoleDTO> roleList;
    private List<RecruitInterviewDto> interviewList;
    @JsonProperty("isAnswered")
    private boolean isAnswered;
}
