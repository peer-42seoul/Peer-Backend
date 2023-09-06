package peer.backend.dto.security.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class EmailAddress {
    @NotBlank(message = "코드를 받을 이메일은 필수항목입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String address;
}
