package peer.backend.entity.team;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobScope;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.team.enums.TeamUserStatus;
import peer.backend.entity.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "team_user")
public class TeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "team_id")
    private Long teamId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;

    @Column(columnDefinition = "TEXT")
    private String review;

    @Enumerated
    private TeamUserStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamUserRoleType role;

    @ManyToMany
    @JoinTable(name = "team_user_job",
            joinColumns = @JoinColumn(name = "team_user_id"),
            inverseJoinColumns = @JoinColumn(name = "team_job_id")
    )
    private List<TeamJob> jobs;

    @ElementCollection
    private List<String> answers;

    public void grantLeader(TeamUserRoleType teamUserRoleType) {
        this.role = teamUserRoleType;
    }

    public void addJob(TeamJob job) {
        if (jobs == null) {
            jobs = new ArrayList<>();
        }
        jobs.add(job);
    }

    public void acceptApplicant(){
        this.status = TeamUserStatus.APPROVED;
    }
}
