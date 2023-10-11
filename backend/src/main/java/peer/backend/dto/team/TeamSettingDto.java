package peer.backend.dto.team;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.TeamUser;

import java.util.ArrayList;
import java.util.SplittableRandom;

@Getter
public class TeamSettingDto {
    private String id;
    private String name;
    private String status;
    private String maxMember;
    private String type;
    private String dueTo;
    private String operationForm;
    private String[] region;
    private ArrayList<TeamMemberDto> member;

    public TeamSettingDto(Team team) {
        this.id = team.getId().toString();
        this.name = team.getName();
        this.dueTo = team.getDueTo();
        this.status = team.getStatus().getValue();
        this.operationForm = team.getOperationFormat().getValue();
        this.type = team.getType().getValue();
        this.region = new String[]{
                team.getRegion1(),
                team.getRegion2(),
                team.getRegion3()
        };
        this.maxMember = team.getMaxMember().toString();
        this.member = new ArrayList<>();
        for (TeamUser teamUser: team.getTeamUsers()) {
            this.member.add(new TeamMemberDto(teamUser));
        }
    }
}
