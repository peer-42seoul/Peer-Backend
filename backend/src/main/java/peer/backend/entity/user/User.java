package peer.backend.entity.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.Authentication;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.message.MessageIndex;
import peer.backend.entity.team.TeamUser;
import peer.backend.entity.user.enums.Role;
import peer.backend.oauth.PrincipalDetails;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@DynamicUpdate
@DynamicInsert
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(length = 10, nullable = false)
    private String name;
    @Column(length = 10, unique = true, nullable = false)
    private String nickname;
    @Column//(nullable = false)
    private LocalDate birthday;
    @Column(columnDefinition = "boolean not null default false")
    private boolean isAlarm;
    @Column//(nullable = false)
    private String phone;
    @Column//(nullable = false)
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
    @Column
    private String keywordAlarm;
    @Column(columnDefinition = "varchar(255) not null default 'ROLE_USER'")
    @Enumerated(EnumType.STRING)
    private Role role;

//    @OneToMany(mappedBy = "user")
//    private List<UserPushKeyword> userPushKeywords = new ArrayList<>();

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitFavorite> recruitFavorites = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<SocialLogin> socialLogins = new ArrayList<>();

    @OneToMany(mappedBy = "user1", cascade = CascadeType.PERSIST)
    private List<MessageIndex> indexList1 = new ArrayList<>();

    @OneToMany(mappedBy = "user2", cascade = CascadeType.PERSIST)
    private List<MessageIndex> indexList2 = new ArrayList<>();

    public static User authenticationToUser(Authentication authentication) {
        return ((PrincipalDetails) authentication.getPrincipal()).getUser();
    }
}
