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
    private long msgId;

    @Column(nullable = false)
    private long senderId;

    @Column(nullable = false)
    private String senderNickname;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime readAt;

    @Column(nullable = false)
    private String text;

    @Column(insertable = false, updatable = false)
    private long conversationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversationId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MessageIndex index;
}
