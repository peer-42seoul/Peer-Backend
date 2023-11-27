package peer.backend.entity.alarm;

import javax.persistence.*;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.alarm.enums.Priority;
import peer.backend.entity.alarm.enums.TargetType;
//import peer.backend.entity.alarm.enums.MessageType;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Alarm")
public class Alarm extends BaseEntity {

    @Id
    @Column(name = "alarm_id")
    private Long id;
    @Column
    private String title;
    @Column
    private String message;
    //    private MessageType messageType;
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
    private Date scheduledTime;
}
