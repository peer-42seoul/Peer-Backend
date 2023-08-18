package peer.backend.entity.composite;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeamUserPK implements Serializable {

    final private Long userId;
    final private Long teamId;
}
