package peer.backend.dto;

import java.util.List;
import lombok.Getter;
import peer.backend.entity.AlarmTargetType;

@Getter
public class AlarmResponse {

    private Long alarmId;
    private String title;
    private String content;
    private AlarmTargetType alarmTargetType;
    private List<AlarmResponseContainable> targetList;
}
