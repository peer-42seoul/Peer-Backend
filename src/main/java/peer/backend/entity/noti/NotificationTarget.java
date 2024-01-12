package peer.backend.entity.noti;

import lombok.*;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.user.User;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification_target")
public abstract class NotificationTarget extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long notificationId;

    @Column(nullable = false)
    private Long userId;

    @Column
    private boolean keywordOk;

    @Column
    private boolean teamOK;

    @Column
    private boolean messageOk;

    @Column
    private boolean nightOk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_event_id")
    private Notification specificEvent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_user")
    private User user;

    public void setAlarmOptions(User user) {
        this.keywordOk = user.isKeywordRecommendAlarm();
        this.teamOK = user.isTeamAlarm();
        this.messageOk = user.isMessageAlarm();
        this.nightOk = user.isNightAlarm();
        this.userId = user.getId();
    }
}
