package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.exception.BadRequestException;

import java.util.List;
import java.util.Optional;

@Getter
public class TeamInfoResponse {
    private final Long id;
    private final String name;
    private final String teamPicturePath;
    private final String status;
    private final int memberCount;
    private final String leaderName;
    private final String createdAt;

    public TeamInfoResponse(Team team) {
        List<TeamUser> teamUserList = team.getTeamUsers();
        this.id = team.getId();
        this.name = team.getName();
        this.teamPicturePath = team.getTeamLogoPath();
        Optional<TeamUser> preValue  = teamUserList.stream().filter(teamUser -> teamUser.getRole().toString().equals("LEADER")).findFirst();
        if (preValue.isEmpty()) {
            throw new BadRequestException("There is no target");
        }
        this.leaderName = preValue.get().getUser().getNickname();
        this.status = team.getStatus().toString();
        this.memberCount = teamUserList.size();
        this.createdAt = String.format(team.getCreatedAt().getYear() + "." + team.getCreatedAt().getMonthValue() + "." + team.getCreatedAt().getDayOfMonth());
    }
}

