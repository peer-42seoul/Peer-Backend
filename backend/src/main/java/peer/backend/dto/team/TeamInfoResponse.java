package peer.backend.dto.team;

import lombok.Getter;
import peer.backend.entity.team.Team;

@Getter
public class TeamInfoResponse {
    private Long id;
    private String name;
//    private List<String> tags;
    private String operationFormat;
    private String status;
    private String region;
    private int maxMember;
    private int memberCount;
    private String dueTo;

    public TeamInfoResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.operationFormat = team.getOperationFormat().getValue();
        this.status = team.getStatus().getValue();
        this.region = team.getRegion1();
        this.maxMember = team.getMaxMember();
        this.memberCount = team.getTeamUsers().size();
        this.dueTo = team.getDueTo();
//        this.tags = team.get
    }
}
