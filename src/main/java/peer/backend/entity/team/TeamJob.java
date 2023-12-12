package peer.backend.entity.team;

import lombok.*;
import peer.backend.entity.board.recruit.Recruit;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team_job")
public class TeamJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_job_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    @Column(nullable = false, length = 10)
    private String name;
    @Column(nullable = false)
    private Integer number;
}