package peer.backend.entity.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.util.ObjectUtils;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.RecruitFavorite;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.board.team.PostLike;
import peer.backend.entity.message.MessageIndex;
import peer.backend.entity.noti.NotificationSubscriptionKeys;
import peer.backend.entity.noti.NotificationTarget;
import peer.backend.entity.tag.UserSkill;
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
@NamedEntityGraph(name="User.withSkills"
                    , attributeNodes = @NamedAttributeNode("skills"))
public class User extends BaseEntity implements Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean activated = true;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Size(min = 2, max = 4)
    private String name;

    @Column(unique = true, nullable = false)
    @Size(min = 2, max = 30)
    private String nickname;

    //TODO : 사용자 알림 설정 API 작성이 필요
    @Column
    private boolean keywordRecommendAlarm;
    //TODO : 사용자 알림 설정 API 작성이 필요
    @Column
    private boolean teamAlarm;
    //TODO : 사용자 알림 설정 API 작성이 필요
    @Column
    private boolean messageAlarm;
    //TODO : 사용자 알림 설정 API 작성이 필요
    @Column
    private boolean nightAlarm;

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

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private PeerOperation peerOperation;

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserLink> userLinks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<TeamUser> teamUsers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<RecruitFavorite> recruitFavorites = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<SocialLogin> socialLogins = new ArrayList<>();

    @OneToMany(mappedBy = "user1", cascade = CascadeType.PERSIST)
    private List<MessageIndex> indexList1 = new ArrayList<>();

    @OneToMany(mappedBy = "user2", cascade = CascadeType.PERSIST)
    private List<MessageIndex> indexList2 = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.PERSIST)
    private List<Recruit> recruitList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Post> post;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserPortfolio> myPortfolios;

    @Column(insertable = true, updatable = true)
    private boolean visibilityForPortfolio = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PostLike> postLikes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<NotificationSubscriptionKeys> tokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<NotificationTarget> myEvents;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserSkill> skills;

    public static User authenticationToUser(Authentication authentication) {
        return (User) ((PrincipalDetails) authentication.getPrincipal()).getUser();
    }

    public void addSocialLogin(SocialLogin socialLogin) {
        if (ObjectUtils.isEmpty(this.socialLogins)) {
            this.socialLogins = new ArrayList<>();
        }
        this.socialLogins.add(socialLogin);
    }

    public Collection<Post> getPost() {
        return post;
    }
}
