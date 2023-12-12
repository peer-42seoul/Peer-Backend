package peer.backend.entity.tag;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.entity.board.recruit.Recruit;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recruit_tag")
public class RecruitTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recruit_id")
    private Long recruitId;

    @Column(name = "tag_id")
    private Long tagId;

    @ManyToOne
    @JoinColumn(name = "recruit_id", insertable = false, updatable = false)
    private Recruit recruit;

    @ManyToOne
    @JoinColumn(name = "tag_id", insertable = false, updatable = false)
    private Tag tag;

    public RecruitTag(Long recruitId, Long tagId) {
        this.recruitId = recruitId;
        this.tagId = tagId;
    }
}
