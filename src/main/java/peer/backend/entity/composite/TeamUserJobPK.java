package peer.backend.entity.composite;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamUserJobPK implements Serializable {
    private Long teamUserId;
    private Long teamJobId;
}
