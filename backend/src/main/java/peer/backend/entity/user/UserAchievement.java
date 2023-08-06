package peer.backend.entity.user;

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
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 업적 테이블 순환 문제 발생
     */
//    @OneToMany(mappedBy = "achievement")
//    private List<Achievement> achievements = new ArrayList<>();

    private LocalDateTime created;
}
