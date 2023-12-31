package peer.backend.entity.message;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MessageIndex extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long conversationId;

    @Column
    private Long userIdx1;

    @Column
    private Long userIdx2;

    @Column(nullable = true)
    private Long unreadMessageNumber1;

    @Column(nullable = true)
    private Long unreadMessageNumber2;

    @Column()
    @Builder.Default
    private boolean user1delete = false;

    @Column()
    @Builder.Default
    private boolean user2delete = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user1_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User user1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user2_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private User user2;

    @OneToMany(mappedBy = "index", cascade = CascadeType.ALL)
    private List<MessagePiece> messagePieces = new ArrayList<>();
}
