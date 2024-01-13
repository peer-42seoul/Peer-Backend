package peer.backend.dto.profile.response;

import lombok.Getter;
import peer.backend.annotation.CustomSize;
import peer.backend.annotation.OnlyEngKorNum;

@Getter
public class NicknameResponse {
    @OnlyEngKorNum(message = "반드시, 영어, 한글, 숫자로만 닉네임을 설정해주셔야 합니다.")
    @CustomSize(min = 2, max = 30, message = "닉네임은 최소 2자, 최대 30자까지 입력 가능합니다.")
    private String nickname;
}
