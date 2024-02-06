package peer.backend.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordDTO {

    @NotBlank(message = "검색할 내용을 추가하세요.")
    @Size(min=2, max=10, message = "검색 키워드는 최소 2글자, 최대 10자까지 검색이 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = "키워드 검색시 특수문자는 사용하실 수 없습니다")
    private String keyword; // 대화 상대
}
