package peer.backend.dto.dndSub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberDTO memberDTO = (MemberDTO) o;
        return Objects.equals(userId, memberDTO.userId) &&
                Objects.equals(nickname, memberDTO.nickname);
    }
    // HashSet 을 제대로 동작시키기 위한 코드, 같은지 여부를 다각도로 점검한다.

    @Override
    public int hashCode() {
        return Objects.hash(userId, nickname);
    }
}
