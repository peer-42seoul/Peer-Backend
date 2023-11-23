package peer.backend.dto.board.recruit;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;

import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitCreateRequest {
    private String image;
    private String name;
    private String title;
    private String due;
    private String place;
    private String type;
    private String content;
    private List<String> region;
    private String link;
    private List<TagListResponse> tagList;
    private List<RecruitRoleDTO> roleList;
    private List<RecruitInterviewDto> interviewList;
}
