package peer.backend.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Msg {
    private long userId;
    private long msgId;
    private String content;
    private String date;

    @JsonProperty("isEnd")
    private boolean end;
}
