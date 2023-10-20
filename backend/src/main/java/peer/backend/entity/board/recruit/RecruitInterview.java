package peer.backend.entity.board.recruit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.entity.board.recruit.enums.RecruitInterviewType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    @Column(nullable = false, length = 255)
    private String question;
    @Enumerated(EnumType.STRING)
    private RecruitInterviewType type;
    @ElementCollection
    private List<String> options;
}
