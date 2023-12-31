package peer.backend.entity.blacklist;

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
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.converter.BacklistTypeConverter;
import peer.backend.entity.user.User;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "blacklist", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class Blacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Convert(converter = BacklistTypeConverter.class)
    private BlacklistType type;

    @Column(nullable = false)
    private String content;

    public Blacklist(User user, BlacklistType type, String content) {
        this.user = user;
        this.type = type;
        this.content = content;
    }
}
