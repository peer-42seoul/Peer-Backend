package peer.backend.dto.tag;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ModifyTagRequest {

    @NotNull
    private Long typeId;

    @NotBlank
    private String name;

    @NotBlank
    private String color;
}
