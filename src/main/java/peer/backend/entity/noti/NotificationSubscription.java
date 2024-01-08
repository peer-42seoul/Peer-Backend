package peer.backend.entity.noti;

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
@Table(name = "notification_subscription")
public class NotificationSubscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    @Column(name = "firebase_token")
    private String firebaseToken;

    @ManyToOne()
    @JoinColumn(name = "id")
    private User user;
}
