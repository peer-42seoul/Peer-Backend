package peer.backend.entity.user;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.DynamicUpdate;
import peer.backend.entity.team.TeamUser;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String userId;
    @Column//(length = 20)
    private String password;
    @Column(length = 10, nullable = false)
    private String name;
    @Column(length = 100, unique = true, nullable = false)
    private String email;
    @Column(length = 10, unique = true, nullable = false)
    private String nickname;
    @Column(nullable = false)
    private LocalDate birthday;
    @Column(nullable = false)
    private boolean isAlarm;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String address;
    @Column
    private String imageUrl;
    @Column//(nullable = false)
    private boolean certification;
    @Column//(nullable = false)
    private String company;
    @Column//(nullable = false)
    private String introduce;
    @Column//(nullable = false)
    private Long peerLevel;
    @Column//(nullable = false)
    private String representAchievement;

    @OneToMany(mappedBy = "user")
    private List<UserPushKeyword> userPushKeywords = new ArrayList<>();

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private PeerOperation peerOperation;

    @OneToMany(mappedBy = "user")
    private List<UserAchievement> userAchievements = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserLink> userLinks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<TeamUser> teamUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<InterestedProject> interestedProjects = new ArrayList<>();
}
