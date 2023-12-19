package peer.backend.entity.team;

import lombok.*;
import peer.backend.entity.composite.TeamUserJobPK;
import peer.backend.entity.team.enums.TeamMemberStatus;
import peer.backend.entity.team.enums.TeamUserStatus;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team_user_job")
@IdClass(TeamUserJobPK.class)
public class TeamUserJob {

    @Id
    @Column(name = "team_user_id")
    private Long teamUserId;

    @Id
    @Column(name = "team_job_id")
    private Long teamJobId;

    @MapsId("teamUserId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_user_id")
    private TeamUser teamUser;

    @MapsId("teamJobId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_job_id")
    private TeamJob teamJob;

    @Enumerated(EnumType.STRING)
    @Column
    private TeamUserStatus status;

    public void acceptApplicant(){
        this.status = TeamUserStatus.APPROVED;
    }
}
