package peer.backend.dto.socket;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.entity.chat.Chat;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {

    private Long userId;

    private String userName;

    private Long teamId;

    private String message;

    private LocalDateTime date;

    public ChatDTO(Chat chat) {
        this.userId = chat.getUserId();
        this.userName = chat.getUserName();
        this.teamId = chat.getTeamId();
        this.message = chat.getMessage();
        this.date = chat.getDate();
    }
}
