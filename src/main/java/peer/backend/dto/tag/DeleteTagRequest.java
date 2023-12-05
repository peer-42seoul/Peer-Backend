package peer.backend.dto.tag;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeleteTagRequest {

    @NotBlank
    private String name;
}
