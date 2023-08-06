package peer.backend.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String user_id;
    @Column(length = 20)
    private String password;
    @Column(length = 10, nullable = false)
    private String name;
    @Column(length = 100, unique = true, nullable = false)
    private String email;
    @Column(length = 10, unique = true, nullable = false)
    private String nickname;
    @Column(nullable = false)
    private LocalDateTime birthday;
    @Column(nullable = false)
    private boolean is_alarm;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String address;

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserLink> userLinks = new ArrayList<>();

    @OneToMany(mappedBy = "user_achievement")
    private List<UserAchievement> userAchievements = new ArrayList<>();

    @OneToMany(mappedBy = "user_push_keyword")
    private List<UserPushKeyword> userPushKeywords = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peer_operation", unique = true, nullable = false)
    private PeerOperation peerOperation;
}
