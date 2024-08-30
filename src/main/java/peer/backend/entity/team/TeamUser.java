package peer.backend.entity.team;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.team.enums.TeamUserStatus;
import peer.backend.entity.user.User;

@Entity
@Getter
@Setter
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamUserRoleType role;

    @OneToMany(mappedBy = "teamUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamUserJob> teamUserJobs;

    @Column
    @Enumerated(EnumType.STRING)
    private TeamUserStatus status;

    @Column(name = "last_read_time")
    private LocalDateTime lastReadTime;

    public void grantLeader(TeamUserRoleType teamUserRoleType) {
        this.role = teamUserRoleType;
    }

    public void grantMember() {
        this.status = TeamUserStatus.APPROVED;
    }

    public void addTeamUserJob(TeamUserJob teamUserJob) {
        if (teamUserJobs == null) {
            teamUserJobs = new ArrayList<>();
        }
        teamUserJobs.add(teamUserJob);
    }
}
