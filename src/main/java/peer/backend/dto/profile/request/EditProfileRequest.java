package peer.backend.dto.profile.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
public class EditProfileRequest {
    private MultipartFile profileImage;
    private String imageChange;
    @NotNull(message = "닉네임은 반드시 입력해야 합니다.")
    @Size(min = 2, max = 30, message = "닉네임은 최소 2자, 최대 30자까지 입력 가능합니다.")
    private String nickname;
    @Size(min = 0, max = 150, message = "자기 소개글은 최대 150자까지만 입력 가능합니다.")
    private String introduction;
}
