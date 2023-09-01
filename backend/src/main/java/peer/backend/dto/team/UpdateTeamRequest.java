package peer.backend.dto.team;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamOperationFormat;
import peer.backend.entity.team.enums.TeamStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.annotation.ValidEnum;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateTeamRequest {

    @NotBlank(message = "이름이 비어있습니다!")
    private String name;

    @NotNull(message = "팀 타입이 비어있습니다!")
    @ValidEnum(enumClass = TeamType.class)
    private TeamType type;

    @NotBlank(message = "기간이 비어있습니다!")
    private String dueTo;

    private String teamPicturePath;

    @NotNull(message = "팀 활동 방식이 비어있습니다!")
    @ValidEnum(enumClass = TeamOperationFormat.class)
    private TeamOperationFormat operationFormat;

    private String teamLogoPath;

    @NotNull(message = "팀 상태가 비어있습니다!")
    @ValidEnum(enumClass = TeamStatus.class)
    private TeamStatus status;

    @NotNull(message = "팀 모집 현황이 비어있습니다!")
    @ValidEnum(enumClass = TeamMemberStatus.class)
    private TeamMemberStatus teamMemberStatus;

    @NotNull(message = "수정 잠금 여부가 비어있습니다!")
    private Boolean isLock;

    private Integer maxMember;

    @NotBlank(message = "지역1이 비어있습니다!")
    private String region1;

    @NotBlank(message = "지역2가 비어있습니다!")
    private String region2;

    @NotBlank(message = "지역3이 비어있습니다!")
    private String region3;
}
