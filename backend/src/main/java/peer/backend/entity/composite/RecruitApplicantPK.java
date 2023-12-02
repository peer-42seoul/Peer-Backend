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
public class RecruitApplicantPK implements Serializable {
    private Long userId;
    private Long recruitId;
    private String role;
}
