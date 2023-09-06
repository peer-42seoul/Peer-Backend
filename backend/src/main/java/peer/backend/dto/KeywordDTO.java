package peer.backend.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordDTO {

    @NotBlank(message = "키워드가 비어있습니다!")
    private String keyword;
}
