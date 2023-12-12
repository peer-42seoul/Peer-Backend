package peer.backend.entity.composite;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class RecruitTagPK implements Serializable {

    @Column(name = "recruit_id")
    private Long recruitId;

    @Column(name = "tag_id")
    private Long tagId;
}
