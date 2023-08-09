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

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_achievement")
public class UserAchievement implements Serializable {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "achievement_id")
    private Long achievementId;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    @MapsId("achievementId")
    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    private LocalDateTime created;
}
