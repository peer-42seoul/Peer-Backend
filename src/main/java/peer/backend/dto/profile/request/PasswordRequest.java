package peer.backend.dto.profile.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequest {

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 반드시 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$",
        message = "대소문자, 숫자, 특수문자를 포함해야 합니다!")
    private String password;
}
