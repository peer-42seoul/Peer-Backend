package peer.backend.dto.board.recruit;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.dto.tag.TagResponse;
import peer.backend.dto.team.TeamJobDto;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitResponce {

    private String title;
    private int totalNumber;
    private RecruitStatus status;
    private String due;
    private String content;
    private Long leader_id;
    private List<String> region;
    private String link;
    private String leader_nickname;
    private String leader_image;
    private List<TagResponse> tagList;
    private List<TeamJobDto> roleList;
    private TeamOperationFormat place;
    private String image;
    private String teamName;
    private boolean isFavorite;
}
