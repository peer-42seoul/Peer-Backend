package peer.backend.dto.profile.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class EditProfileRequest {
    private MultipartFile profileImage;
    private Boolean imageChange;
    private String nickname;
    private String introduction;
}
