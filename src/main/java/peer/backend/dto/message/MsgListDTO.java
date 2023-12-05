package peer.backend.dto.message;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MsgListDTO {
    MsgOwner msgOwner;
    MsgTarget msgTarget;
    List<Msg> msgList;
}
