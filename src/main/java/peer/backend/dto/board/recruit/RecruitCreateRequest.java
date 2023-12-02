package peer.backend.dto.board.recruit;

import lombok.*;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;


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
    private List<TagListResponse> tagList;
    private List<RecruitRoleDTO> roleList;
    private List<RecruitInterviewDto> interviewList;
}
