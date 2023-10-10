package peer.backend.dto.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import peer.backend.entity.user.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    @NotBlank(message = "비밀번호는 필수항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 반드시 8자 이상이어야 합니다.")
    private String password;
    @NotBlank(message = "이름은 필수항목입니다.")
    private String name;
    @NotBlank(message = "이메일은 필수항목입니다.")
    @Email(message = "이메일형식에 맞지 않습니다.")
    private String email;
    @NotBlank(message = "닉네임은 필수항목입니다.")
    private String nickname;
    private boolean isAlarm = false;
    @NotBlank(message = "주소는 필수항목입니다.")
    private String address;

    public User convertUser() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return User.builder()
            .password(encoder.encode(this.password))
            .name(this.name)
            .email(this.email)
            .nickname(this.nickname)
            .isAlarm(this.isAlarm)
            .address(this.address)
            .build();
    }
}
