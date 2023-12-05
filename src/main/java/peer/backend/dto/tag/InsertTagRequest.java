package peer.backend.dto.tag;

import javax.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class InsertTagRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String color;
}