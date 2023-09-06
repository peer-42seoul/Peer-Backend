package peer.backend.entity.user;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.achievement.Achievement;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import peer.backend.entity.composite.UserAchievementPK;

@Entity
@Getter
@NoArgsConstructor
@IdClass(UserAchievementPK.class)
@Table(name = "user_achievement")
public class UserAchievement {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "achievement_id")
    private Long achievementId;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("achievementId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    @Column(nullable = false)
    private LocalDateTime created;

    public UserAchievement(Long userId, Long achievementId, User user, Achievement achievement,
        LocalDateTime created) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.user = user;
        this.achievement = achievement;
        this.created = created;
    }
}
