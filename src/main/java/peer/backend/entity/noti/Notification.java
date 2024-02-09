package peer.backend.entity.noti;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import peer.backend.dto.noti.enums.NotificationPriority;
import peer.backend.dto.noti.enums.NotificationType;
import peer.backend.dto.noti.enums.TargetType;
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
    public Long id;

    @Column
    public String imageUrl;

    @Column
    public String title;

    @Column
    public String body;

    @Column
    public String linkData;

    @Column(nullable = false)
    public TargetType targetType;

    @Column
    public Boolean sent;

    @Column(nullable = false)
    public NotificationPriority priority;

    @Column(nullable = false)
    public NotificationType messageType;

    @Column(nullable = true)
    @JsonSerialize
    @JsonDeserialize
    public LocalDateTime scheduledTime;

    @Column
    public Long referenceCounter;
}
