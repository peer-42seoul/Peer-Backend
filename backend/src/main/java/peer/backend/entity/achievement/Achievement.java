package peer.backend.entity.achievement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.user.UserAchievement;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "achievement")
    private List<UserAchievement> userAchievements = new ArrayList<>();

    public Achievement(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
