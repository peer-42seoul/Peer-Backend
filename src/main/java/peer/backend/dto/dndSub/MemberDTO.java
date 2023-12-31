package peer.backend.dto.dndSub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    @NotBlank
    @Min(value = 1, message = "정상적인 userId를 넣어 주십시오.")
    private Long userId;
    @NotBlank(message = "닉네임은 필수 요소 입니다.")
    private String nickname;
}
