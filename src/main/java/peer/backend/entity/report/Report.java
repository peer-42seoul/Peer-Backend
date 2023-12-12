package peer.backend.entity.report;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.converter.ReportStatusConverter;
import peer.backend.converter.ReportTypeConverter;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.user.User;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "report")
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "from_user")
    private User fromUser;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "to_user")
    private User toUser;

    @Column(nullable = false)
    @Convert(converter = ReportTypeConverter.class)
    private ReportType type;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Convert(converter = ReportStatusConverter.class)
    private ReportStatus status;

    public Report(User from, User to, ReportType type, String content) {
        this.fromUser = from;
        this.toUser = to;
        this.type = type;
        this.content = content;
        this.status = ReportStatus.WAITING;
    }
}
