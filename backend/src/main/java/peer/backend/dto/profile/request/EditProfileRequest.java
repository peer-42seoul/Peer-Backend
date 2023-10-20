package peer.backend.dto.profile.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class EditProfileRequest {
    private MultipartFile profileImage = null;
    private boolean imageChange;
    private String nickname;
    private String introduction;
}
