package peer.backend.dto.tag;

import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DeleteTagRequest {

    @NotNull
    private String tag;
}
