package peer.backend.dto.tag;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateTagRequest extends CreateTagRequest {

    @NotNull
    private Long tagId;
}
