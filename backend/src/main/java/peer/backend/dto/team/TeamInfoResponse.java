package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;

import java.util.List;

@Getter
public class TeamInfoResponse {
    private final Long id;
    private final String name;
    private final String teamPicturePath;
    private final String status;
    private final String memberCount;
    private final String leaderName;
    private final String dueTo;
    private final String operationFormat;
    private final String[] region;

    public TeamInfoResponse(Team team) {
        List<TeamUser> teamUserList = team.getTeamUsers();
        this.id = team.getId();
        this.name = team.getName();
        this.teamPicturePath = team.getTeamPicturePath();
        this.operationFormat = team.getOperationFormat().getValue();
        this.leaderName= teamUserList.stream().filter(teamUser -> teamUser.getRole().toString().equals("LEADER")).findFirst().get().getUser().getNickname();
        this.status = team.getStatus().getValue();
        this.region = new String[]{
                team.getRegion1(),
                team.getRegion2(),
                team.getRegion3()
        };
        this.memberCount = String.format(teamUserList.size() + " / " + team.getMaxMember());
        this.dueTo = team.getDueTo();
    }
}
