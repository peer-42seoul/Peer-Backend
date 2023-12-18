package peer.backend.dto.blacklist;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import peer.backend.annotation.ValidEnum;
import peer.backend.entity.blacklist.BlacklistType;

@Getter
public class AddBlacklistRequest {

    @NotBlank(message = "이메일은 필수 입니다.")
    @Email
    private String email;

    @ValidEnum(enumClass = BlacklistType.class)
    private BlacklistType type;

    @NotBlank(message = "정지 사유는 필수입니다.")
    private String content;
}
