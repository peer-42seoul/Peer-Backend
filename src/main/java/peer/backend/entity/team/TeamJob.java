package peer.backend.entity.team;

import lombok.*;
import peer.backend.entity.board.recruit.Recruit;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToMany(mappedBy = "jobs")
    List<TeamUser> users = new ArrayList<>();

    @Column(nullable = false, length = 10)
    private String name;
    @Column(nullable = false)
    private Integer max = 0;
    @Column
    private Integer current = 0;

    @PrePersist
    @PreUpdate
    private void updateValues() {
        this.current = (users == null ? 0 :users.size());
    }


}