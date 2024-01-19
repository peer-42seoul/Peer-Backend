package peer.backend.entity.user;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.team.Post;
import peer.backend.entity.team.Team;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_portfiolio")
@DynamicUpdate
@DynamicInsert
public class UserPortfolio extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user; // User, 1:N fetch.LAZY 전략

    @Column
    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_team_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Team team;//  : team, 1: N fetch.LAZY 전략

    @Column
    private String teamName;

    @Column
    private String teamLogo;

    @Column
    private String recruitImage;

    @Column
    private Long recruitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_recruit_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Recruit recruit;// : recruit 타입 객체에 연결되는 용도. fetch.LAZY 전략

    @Column
    private Long showcaseId = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_showcase_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;// : 게시글, 1:N 으로 게시글에 연결되어 있다. fetch.LAZY 전략

    @Column
    private Long peerlogId = 0L; // : Long // 일단은 peerlog 기능 전까지는 임시로 존재만한다.

    @Column
    private boolean visibility = true;// : boolean, default 는 true다. user 의 속성을 따라 지정된다.
}
