package peer.backend.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO {

    @NotBlank(message = "비밀번호가 필요합니다!")
    private String password;
}
