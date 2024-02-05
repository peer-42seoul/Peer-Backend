package peer.backend.dto.profile.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.annotation.CustomSize;
import peer.backend.annotation.OnlyEngKorNum;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
public class EditProfileRequest {
    private MultipartFile profileImage;
    private String imageChange;
    @CustomSize(min = 2, max = 30, message = "닉네임은 최소 2자, 최대 30자까지 입력 가능합니다.")
    @OnlyEngKorNum(message = "반드시, 영어, 한글, 숫자로만 닉네임을 설정해주셔야 합니다.")
    @NotNull(message = "닉네임은 반드시 입력해야 합니다.")
    private String nickname;
    @CustomSize(min = 0, max = 150, message = "자기 소개문은 최대 150자까지 입력 가능합니다.")
    private String introduction;
}
