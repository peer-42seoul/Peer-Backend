package peer.backend.dto.board.recruit;

import lombok.*;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;
import peer.backend.entity.board.recruit.enums.RecruitStatus;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
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
    private String name;
    @NotNull
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
