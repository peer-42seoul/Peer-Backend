package peer.backend.entity.alarm;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.alarm.enums.Priority;
import peer.backend.entity.alarm.enums.TargetType;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Alarm")
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;
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
    @Column()
    private Date scheduledTime;

}
