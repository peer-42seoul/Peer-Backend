package peer.backend.entity.noti;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.dto.noti.enums.MessageType;
import peer.backend.dto.noti.enums.NotificationPriority;
import peer.backend.dto.noti.enums.TargetType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column
    public String title;

    @Column
    public String body;

    @Column
    public String linkData;

    @Column(nullable = false)
    public TargetType targetType;

    @OneToMany(mappedBy = "specificEvent",
            cascade = CascadeType.PERSIST)
    public List<NotificationTarget> targets;

    @Column
    public Boolean sent;

    @Column(nullable = false)
    public NotificationPriority priority;

    @Column(nullable = false)
    public MessageType messageType;

    @Column
    @JsonSerialize
    @JsonDeserialize
    public LocalDateTime scheduledTime;

    @Column
    public Long totalCount;

    @Column
    public Long deleteCount;
}
