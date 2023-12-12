package peer.backend.entity.tag;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.composite.RecruitTagPK;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recruit_tag")
public class RecruitTag {

    @EmbeddedId
    RecruitTagPK id = new RecruitTagPK();

    @ManyToOne
    @MapsId("recruitId")
    @JoinColumn(name = "recruit_id")
    Recruit recruit;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    Tag tag;

    public RecruitTag(Recruit recruit, Tag tag) {
        this.recruit = recruit;
        this.tag = tag;
    }
}
