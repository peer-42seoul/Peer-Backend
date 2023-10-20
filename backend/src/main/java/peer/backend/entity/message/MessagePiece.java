package peer.backend.entity.message;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import peer.backend.entity.BaseEntity;
import peer.backend.entity.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Comparator;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MessagePiece extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long msgId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String senderNickname;

    @Column(nullable = true)
    private LocalDateTime readAt;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String text;

    @Column()
    private Long targetConversationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_conversationId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MessageIndex index;
}
