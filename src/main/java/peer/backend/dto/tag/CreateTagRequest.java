package peer.backend.dto.tag;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateTagRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String color;
}