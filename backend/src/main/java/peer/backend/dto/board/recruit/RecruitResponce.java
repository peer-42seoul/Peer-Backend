package peer.backend.dto.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.board.recruit.RecruitInterview;
import peer.backend.entity.board.recruit.RecruitRole;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.user.enums.Role;

import javax.swing.*;
import java.util.List;

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
    private List<TagListResponse> tagList;
    private List<RecruitRoleDTO> roleList;
    private TeamOperationFormat place;
    private String image;
}
