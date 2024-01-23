package peer.backend.dto.security;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import peer.backend.entity.user.User;
import peer.backend.exception.IllegalArgumentException;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserInfo {
    @NotBlank(message = "이메일은 필수항목입니다.")
    @Email(message = "이메일형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 반드시 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$",
        message = "대소문자, 숫자, 특수문자를 포함해야 합니다!")
    private String password;

    @NotBlank(message = "닉네임은 필수항목입니다.")
    @Size(min = 2, max = 30, message = "2글자 이상 30글자 이하여야 합니다!")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,30}$", message = "한글, 대소문자, 숫자로만 이루어져야 합니다!")
    private String nickname;

    @NotBlank(message = "이름은 필수항목입니다.")
    @Size(min = 2, max = 4, message = "2글자 이상 4글자 이하여야 합니다!")
    @Pattern(regexp = "^[가-힣]{2,4}$", message = "한글로만 이루어져야 합니다!")
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

//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime serviceUseAgrement;

//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime personalInformationUseAgreement;

    public UserInfo(String email, String password, String nickname, String name, String socialEmail,
                    LocalDateTime serviceUseAgrement, LocalDateTime personalInformationUseAgreement) throws IllegalArgumentException {
        String errorMessage = "";
        if (email.isBlank()) {
            errorMessage += "이메일은 필수 항목입니다.\n";
        }
        if(!email.isBlank() && !email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}")) {
            errorMessage += "이메일형식에 맞지 않습니다.\n";
        }
        if (password.isBlank()){
            errorMessage += "비밀번호는 필수항목입니다.\n";
        }
        if (!password.isBlank() && (password.length() < 8 || password.length() > 20)) {
            errorMessage += "비밀번호는 반드시 8자 이상, 20자 이하여야 합니다.\n";
        }
        if (!password.isBlank() && !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$")) {
            errorMessage += "대소문자, 숫자, 특수문자를 포함해야 합니다!\n";
        }
        if (nickname.isBlank()) {
            errorMessage += "닉네임은 필수항목입니다.\n";
        }
        if (!nickname.isBlank() && (nickname.length() < 2 || nickname.length() > 30)) {
            errorMessage += "닉네임은 2글자 이상 30글자 이하여야 합니다.\n";
        }
        if(!nickname.isBlank() && !nickname.matches("^[가-힣a-zA-Z0-9]{2,30}$")) {
            errorMessage +="한글, 대소문자, 숫자로만 이루어져야 합니다!\n";
        }
        if (name.isBlank()) {
            errorMessage += "이름은 필수항목입니다.\n";
        }
        if (!name.isBlank() && (name.length() < 2 || name.length() > 4)) {
            errorMessage += "이름은 2글자 이상 4글자 이하여야 합니다.\n";
        }
        if (!name.isBlank() && !name.matches("^[가-힣]{2,4}$")) {
            errorMessage += "이름은 한글로만 이루어져야 합니다!\n";
        }
        if (socialEmail != null && !socialEmail.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}")) {
            errorMessage += "Social 이메일이 형식에 맞지 않습니다.\n";
        }
        if (!errorMessage.isEmpty())
            throw new IllegalArgumentException(errorMessage);

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.socialEmail = socialEmail;
        this.serviceUseAgrement = serviceUseAgrement;
        this.personalInformationUseAgreement = personalInformationUseAgreement;
    }
    public User convertUser() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return User.builder()
            .email(this.email)
            .password(encoder.encode(this.password))
            .name(this.name)
            .nickname(this.nickname).build();
    }
}
