package peer.backend.dto.tag;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateTagRequest {

    @NotBlank
    @Size(max = 10, message = "길이는 10 이하여야 합니다.")
    private String name;

    @NotBlank
    @Size(max = 7, message = "길이는 10 이하여야 합니다.")
    private String color;
}