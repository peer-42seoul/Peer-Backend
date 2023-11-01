package peer.backend.dto.message;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
public class MsgContentDTO {
    @Min(value = 0l, message = "쪽지 대상이 정확하지 않습니다.")
    @Positive(message = "쪽지 대상이 정확하지 않습니다.")
    private long targetId;

    @NotBlank(message = "빈 쪽지는 보낼수 없습니다.")
    @Length(max = 300, message = "쪽지 허용 최대 길이를 초과하셨습니다.")
    private String content;
}
