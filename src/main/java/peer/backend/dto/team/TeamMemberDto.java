package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamUserRoleType;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TeamMemberDto {

    private Long id;
    private String name;
    private TeamUserRoleType role;
    private List<String> job;
    private String image;

    public TeamMemberDto(TeamUser teamUser) {
        this.id = teamUser.getUserId();
        this.name = teamUser.getUser().getNickname();
        this.role = teamUser.getRole();
        this.job = teamUser.getTeamUserJobs()
            .stream()
            .map(
                teamUserJob -> teamUserJob.getTeamJob().getName())
            .collect(Collectors.toList());
        this.image = teamUser.getUser().getImageUrl();
    }
}
