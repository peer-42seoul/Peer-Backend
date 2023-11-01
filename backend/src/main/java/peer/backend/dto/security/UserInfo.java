package peer.backend.dto.security;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import peer.backend.entity.user.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    //    @NotBlank(message = "ID는 필수항목입니다.")
//    private String userId;
    @NotBlank(message = "이메일은 필수항목입니다.")
    @Email(message = "이메일형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 반드시 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$")
    private String password;

    @NotBlank(message = "닉네임은 필수항목입니다.")
    @Size(min = 2, max = 7)
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,7}$")
    private String nickname;

    @NotBlank(message = "이름은 필수항목입니다.")
    @Size(min = 2, max = 4)
    @Pattern(regexp = "^[가-힣]{2,4}$")
    private String name;
    //    @NotBlank(message = "생년월일은 필수항목입니다.")
//    @Pattern(regexp = "^(\\d{4})-(?:[1-9]|\\d{2})-(?:[1-9]|\\d{2})$", message = "생년월일형식에 맞지 않습니다.")
//    private String birthday;
//    private boolean isAlarm = false;
//    @NotBlank(message = "전화번호는 필수항목입니다.")
//    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-(\\d{4})$", message = "전화번호형식에 맞지 않습니다.")
//    private String phone;
//    @NotBlank(message = "주소는 필수항목입니다.")
//    private String address;
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String socialEmail;

    public User convertUser() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return User.builder()
            .email(this.email)
            .password(encoder.encode(this.password))
            .name(this.name)
            .nickname(this.nickname).build();
    }
}
