package peer.backend.dto.team;

import lombok.*;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.team.Team;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import springfox.documentation.service.Operation;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TeamSettingInfoDto {
    @NotBlank(message = "팀 아이디는 필수입니다.")
    private String id;
    @NotBlank(message = "팀 이름은 필수입니다.")
    private String name;
    private String teamImage;
    @ValidEnum(enumClass = TeamStatus.class, message = "팀 상태가 잘못되었습니다.")
    private TeamStatus status;
    @NotBlank(message = "팀 최대 인원은 필수입니다.")
    private String maxMember;
    @ValidEnum(enumClass = TeamType.class, message = "팀 타입이 잘못되었습니다.")
    private TeamType type;
    @NotBlank(message = "팀 마감일은 필수입니다.")
    private String dueTo;
    @ValidEnum(enumClass = TeamOperationFormat.class, message = "팀 운영형태가 잘못되었습니다.")
    private TeamOperationFormat operationForm;
    private String[] region;

    public TeamSettingInfoDto(Team team) {
        this.id = team.getId().toString();
        this.name = team.getName();
        this.teamImage = team.getTeamLogoPath();
        this.dueTo = team.getDueTo();
        this.status = team.getStatus();
        this.operationForm = team.getOperationFormat();
        this.type = team.getType();
        this.region = new String[]{
                team.getRegion1(),
                team.getRegion2(),
                team.getRegion3()
        };
        this.maxMember = team.getMaxMember().toString();
    }

    public void setTeamImage(String teamImage) {
        this.teamImage = teamImage;
    }
}
