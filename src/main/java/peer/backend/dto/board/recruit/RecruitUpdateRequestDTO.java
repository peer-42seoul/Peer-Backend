package peer.backend.dto.board.recruit;

import lombok.*;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;
import peer.backend.entity.board.recruit.enums.RecruitStatus;

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
    private Long user_id;
    @NotNull
    @Size(min = 2, max = 30, message = "팀 이름은 2글자 이상 30글자 이하로 작성해 주세요.")
    private String name;
    @NotNull(message = "제목이 반드시 필요합니다.")
    @Size(min = 1, max = 100, message = "제목은 1~100글자로 작성해주세요.")
    private String title;
    @NotNull
    private RecruitStatus status;
    @NotNull
    private String due;
    @NotNull
    @Lob
    private String content;
    private List<String> region;
    private String region1 = region.get(0);
    private String region2 = region.get(1);
    private String link;
    private List<TagListResponse> tagList;
    @NotNull
    private String place;
    private List<RecruitRoleDTO> roleList;
    private List<RecruitInterviewDto> interviewList;
    private String image;
}
