package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.entity.team.Team;

@Getter
public class TeamResponse {

    public TeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.type = team.getType().getValue();
        this.dueTo = team.getDueTo();
        this.teamPicturePath = team.getTeamPicturePath();
        this.operationFormat = team.getOperationFormat().getValue();
        this.teamLogoPath = team.getTeamLogoPath();
        this.status = team.getStatus().getValue();
        this.teamMemberStatus = team.getTeamMemberStatus().getValue();
        this.isLock = team.getIsLock();
        this.maxMember = team.getMaxMember();
        this.region1 = team.getRegion1();
        this.region2 = team.getRegion2();
        this.region3 = team.getRegion3();
    }

    private final Long id;
    private final String name;
    private final String type;
    private final String dueTo;
    private final String teamPicturePath;
    private final String operationFormat;
    private final String teamLogoPath;
    private final String status;
    private final String teamMemberStatus;
    private final Boolean isLock;
    private final Integer maxMember;
    private final String region1;
    private final String region2;
    private final String region3;
}
