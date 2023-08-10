package peer.backend.entity.user;

import java.awt.Composite;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_link")
public class UserLink implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @MapsId("userId")
    @ManyToOne(targetEntity = UserProfile.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserProfile userProfile;

    @Column(nullable = false)
    private String link_name;

    private String link_url;
    private String favicon_path;
}
