package peer.backend.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordDTO {

    @NotEmpty(message = "문자가 없으면 검색이 되지 않습니다.")
    @NotNull
    @Size(min=2, max=10, message = "검색 키워드는 최소 2글자, 최대 10자까지 검색이 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "특수문자는 사용할 수 없습니다.")
    private String keyword; // 대화 상대
}
