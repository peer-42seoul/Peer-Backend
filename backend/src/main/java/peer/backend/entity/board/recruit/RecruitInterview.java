package peer.backend.entity.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.entity.board.recruit.enums.RecruitInterviewType;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recruit_interview")
public class RecruitInterview {
    @Id
    @Column(name = "recruit_intreview_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurit_id")
    private Recruit recruit;

    @Column(nullable = false, length = 255)
    private String question;
    @Enumerated(EnumType.STRING)
    private RecruitInterviewType type;
//    질문
//    @OneToMany(mappedBy = "recruit_interview", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<RecruitInterviewOption> options = new ArrayList<>();

}
