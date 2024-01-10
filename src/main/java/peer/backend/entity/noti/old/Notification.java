package peer.backend.entity.noti.old;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.noti.old.enums.Priority;
import peer.backend.entity.noti.old.enums.TargetType;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column
    private String title;

    @Column
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType;

    @Column
    private Long target;

    @Column
    private String link;

    @Column(nullable = false)
    private Boolean sent;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column
    private LocalDateTime scheduledTime;

    @OneToMany(mappedBy = "specificNoti", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<NotificationTarget> targets;
}
