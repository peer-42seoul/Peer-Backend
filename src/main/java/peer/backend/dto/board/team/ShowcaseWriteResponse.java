package peer.backend.dto.board.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.dto.profile.response.UserLinkResponse;
import peer.backend.dto.tag.TagResponse;
import peer.backend.dto.user.UserShowcaseResponse;
import peer.backend.entity.tag.Tag;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamUserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ShowcaseWriteResponse {
    private String name;
    private List<TagResponse> skills;
    private String start;
    private String end;
    private List<UserShowcaseResponse> memberList;

    public ShowcaseWriteResponse(Team team, List<Tag> tags, List<TeamUser> member){
        this.name = team.getName();
        this.skills = tags.stream().map(TagResponse::new).collect(Collectors.toList());
        this.start = team.getCreatedAt().toString();
        this.end = team.getEnd().toString();
        this.memberList = member.stream()
                .filter(teamUser -> teamUser.getStatus().equals(TeamUserStatus.APPROVED))
                .map(UserShowcaseResponse::new).collect(Collectors.toList());
    }
}
