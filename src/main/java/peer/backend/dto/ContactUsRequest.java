package peer.backend.dto;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import peer.backend.annotation.CustomSize;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContactUsRequest {
    @NotBlank(message = "필수 입력 항목입니다.")
    @Size(min = 1, max = 30, message = "올바른 이름을 작성하여 주십시오.")
    private String firstName;

    @NotBlank(message = "필수 입력 항목입니다.")
    @Size(min = 1, max = 30, message = "올바른 이름을 작성하여 주십시오.")
    private String lastName;

    @Email(message = "이메일의 형식을 지켜야 합니다.")
    @Size(min = 1, max = 30, message = "이메일을 제대로 입력하여 주십시오.")
    private String email;

    @Nullable
    @Size(max = 20, message = "회사명을 제대로 입력하여 주십시오.")
    private String company;

    @Nullable
    @Size(max = 30, message = "링크를 제대로 입력하여 주십시오.")
    private String companySite;

    @Nullable
    @Size(max = 500, message = "최대 500자까지 문의사항을 기재할 수 있습니다.")
    private String text;
}
