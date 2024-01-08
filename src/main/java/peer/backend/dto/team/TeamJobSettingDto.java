package peer.backend.dto.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import peer.backend.entity.team.TeamJob;

@Getter
@RequiredArgsConstructor
public class TeamJobSettingDto {
    private Long id;
    private String name;
    private int current;
    private int max;

    public TeamJobSettingDto(TeamJob teamJob){
        this.id = teamJob.getId();
        this.name = teamJob.getName();
        this.current = teamJob.getCurrent();
        this.max = teamJob.getMax();
    }
}
