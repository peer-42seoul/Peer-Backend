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

    private Long id;
    private String name;
    private String type;
    private String dueTo;
    private String teamPicturePath;
    private String operationFormat;
    private String teamLogoPath;
    private String status;
    private String teamMemberStatus;
    private Boolean isLock;
    private Integer maxMember;
    private String region1;
    private String region2;
    private String region3;
}
