package peer.backend.entity.notice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import peer.backend.converter.NoticeStatusConverter;
import peer.backend.converter.NotificationConverter;
import peer.backend.entity.BaseEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "notice")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private Long view;

    @Column(nullable = false)
    @Convert(converter = NoticeStatusConverter.class)
    private NoticeStatus status;

    @Column(nullable = false)
    @Convert(converter = NotificationConverter.class)
    private Notification notification;

    @Column
    private LocalDateTime reservationDate;

    public void setStatus(NoticeStatus status) {
        if (status.equals(NoticeStatus.PUBLISHED)) {
            ZoneId seoulZone = ZoneId.of("Asia/Seoul");
            this.setCreatedAt(LocalDateTime.now(seoulZone));
        }
        this.status = status;
    }
}
