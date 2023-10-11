package peer.backend.entity.team;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.composite.TeamUserPK;
import peer.backend.entity.team.enums.TeamUserRoleType;
import peer.backend.entity.user.User;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TeamUserPK.class)
@Table(name = "team_user")
public class TeamUser {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "team_id")
    private Long teamId;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("teamId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(columnDefinition = "TEXT")
    private String review;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TeamUserRoleType teamUserRoleType;

    public void grantLeader(TeamUserRoleType teamUserRoleType) {
        this.teamUserRoleType = teamUserRoleType;
    }
}
