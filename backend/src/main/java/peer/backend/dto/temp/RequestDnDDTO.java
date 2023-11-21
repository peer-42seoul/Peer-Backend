package peer.backend.dto.temp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDnDDTO {
    private Long teamId;
    private String type;
}
