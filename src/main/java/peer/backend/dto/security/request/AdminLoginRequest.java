package peer.backend.dto.security.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AdminLoginRequest {

    @NotBlank(message = "Id는 필수 항목입니다.")
    private String id;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    private String password;
}
