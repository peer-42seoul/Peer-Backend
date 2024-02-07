package peer.backend.dto.tag;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateTagRequest {

    @NotBlank
    @Max(10)
    private String name;

    @NotBlank
    @Max(7)
    private String color;
}