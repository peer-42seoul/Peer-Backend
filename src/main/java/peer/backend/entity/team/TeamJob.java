package peer.backend.entity.team;

import lombok.*;
import peer.backend.entity.team.enums.TeamUserStatus;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "teamJob", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TeamUserJob> teamUserJobs;

    @Column(nullable = false, length = 10)
    private String name;
    @Column(nullable = false)
    private Integer max;


    public int getCurrent(){
        if (Objects.isNull(this.teamUserJobs))
            return 0;
        else
            return teamUserJobs.stream().filter(
                            job -> job.getStatus().equals(TeamUserStatus.APPROVED))
                    .collect(Collectors.toList()).size();
    }

    public void increase() {
        this.max++;
    }

    public void decrease() {
        if (this.max > 0)
            this.max--;
    }
}