package peer.backend.entity.noti;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import peer.backend.dto.noti.enums.NotificationPriority;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Table(name = "notification")
public  class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String imageUrl;

    @Column
    private String title;

    @Column
    private String body;

    @Column
    private String linkData;

    @Column
    private boolean sent;

    @Column(nullable = false)
    private NotificationPriority priority;

    @Column(nullable = false)
    private NotificationType messageType;

    @Column()
    @JsonSerialize
    @JsonDeserialize
    private LocalDateTime scheduledTime;

    @Column
    private Integer referenceCounter;

    @OneToMany(mappedBy = "specificEvent", cascade = CascadeType.PERSIST)
    private List<NotificationTarget> targetList;
}
