package peer.backend.dto.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.team.enums.TeamUserStatus;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(value = {"approved"})
public class TeamListResponse {

    public TeamListResponse(Team team, TeamUser teamUser, int teamMemberCount) {
        this.id = team.getId().toString();
        this.name = team.getName();
        this.dueTo = team.getDueTo().getLabel();
        this.status = team.getStatus().toString();
        this.role = teamUser.getRole().toString();
        this.type = team.getType().toString();
        this.teamCount = String.format(teamMemberCount + " / " + team.getMaxMember());
        this.region = team.getRegion1();
        this.operationFormat = team.getOperationFormat().toString();
        this.isApproved = teamUser.getStatus().equals(TeamUserStatus.APPROVED);
    }

    private String id;
    private String name;
    private String dueTo;
    private String status;
    private String type;
    private String role;
    private String teamCount; //"3 / 12"
    private String region;
    private String operationFormat;
    @JsonProperty("isApproved")
    private boolean isApproved;
    // TODO: 팀원 권한 추가 예정
}
