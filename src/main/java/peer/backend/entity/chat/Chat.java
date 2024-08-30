package peer.backend.entity.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.team.Team;
import peer.backend.entity.user.User;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat")
public class Chat extends BaseEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @Column(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "team")
    private Team team;

    @Column(name = "message", nullable = false)
    private String message;


}
