package peer.backend.dto.profile.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Builder
public class EditProfileRequest {
    private MultipartFile profileImage;
    private String imageChange;
    @NotNull(message = "닉네임은 반드시 입력해야 합니다.")
    @NotBlank(message = "닉네임은 반드시 입력해야 합니다.")
    @NotEmpty(message = "닉네임은 반드시 입력해야 합니다.")
    private String nickname;
    private String introduction;
}
