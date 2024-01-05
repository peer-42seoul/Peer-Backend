package peer.backend.dto.socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.team.enums.TeamUserRoleType;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class yesWhoUAreDTO {
    public String userId;
    public String teamId;
    public String teamName;
    public TeamUserRoleType yourRole;
}
