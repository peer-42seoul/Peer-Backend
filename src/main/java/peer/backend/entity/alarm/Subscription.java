package peer.backend.entity.alarm;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import peer.backend.entity.user.User;
import peer.backend.entity.BaseEntity;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "subscription")
public class Subscription extends BaseEntity {
    @Id
    @Column(name = "subscription_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "firebase_token")
    private String firebaseToken;
}
