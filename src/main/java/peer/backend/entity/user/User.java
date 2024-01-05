package peer.backend.entity.user;

import java.util.ArrayList;
import java.util.Collection;
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
public class User extends BaseEntity implements Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column//(nullable = false)
    private boolean isAlarm;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PostLike> postLikes;

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
