package peer.backend.dto.team;

import lombok.*;
import peer.backend.entity.team.Team;

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
    @NotBlank(message = "팀 상태는 필수입니다.")
    private String status;
    @NotBlank(message = "팀 최대 인원은 필수입니다.")
    private String maxMember;
    @NotBlank(message = "팀 타입은 필수입니다.")
    private String type;
    @NotBlank(message = "팀 마감일은 필수입니다.")
    private String dueTo;
    @NotBlank(message = "팀 운영형태는 필수입니다.")
    private String operationForm;
//    @NotBlank(message = "팀 지역은 필수입니다.")
    private String[] region;

    public TeamSettingInfoDto(Team team) {
        this.id = team.getId().toString();
        this.name = team.getName();
        this.teamImage = team.getTeamLogoPath();
        this.dueTo = team.getDueTo();
        this.status = team.getStatus().toString();
        this.operationForm = team.getOperationFormat().toString();
        this.type = team.getType().toString();
        this.region = new String[]{
                team.getRegion1(),
                team.getRegion2(),
                team.getRegion3()
        };
        this.maxMember = team.getMaxMember().toString();
    }
}
